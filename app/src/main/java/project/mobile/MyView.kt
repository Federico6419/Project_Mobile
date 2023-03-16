package project.mobile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import android.util.Log
import kotlinx.coroutines.*
import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.withTranslation
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.math.atan2

class MyView(context: Context?,w:String?,old_score: Int?, ID : String?,Username :String?,Password:String?, Color :String?,Bul :String?) : View(context), View.OnTouchListener, SensorEventListener2 {

    val a = 0.5f //Low-pass filter parameter, higher is smoother
    var weather = ""
    var password= ""
    var username = ""

    var mLastRotationVector = FloatArray(3) //The last value of the rotation vector
    var mRotationMatrix = FloatArray(9)
    var roll = 0f
    var up = 0f
    var up2 = 0f
    var up3 = 0f
    var upboss = arrayOf<Float>(0f,0f)
    var shotboss = arrayOf<Boolean>(false,false)
    var boss_bullet_position_x=arrayOf<Float>(0f,0f,0f)
    var boss_bullet_position_y=arrayOf<Float>(0f,0f,0f)
    var just_shot = false ///to avoid multiple collision of single bullet of the boss
    var justcollide = arrayOf<Boolean>(false,false,false,false,false) // to avoid multiple collision of single enemy
    var just_shot_bullet =  arrayOf<Boolean>(false,false,false) // to avoid multiple collision of single bullet of our airplane
    var just_shot_bullet_boss = arrayOf<Boolean>(false,false,false) // to avoid multiple collision of single bullet of our airplane on the boss
    var shot = false
    var shot2 = false
    var shot3 = false
    var res = 0f
    var res2 = 0f
    var res3 = 0f
    var resboss = 0f
    var resbossy = 0f
    var resboss2_1 = 0f
    var resbossy2_1 = 0f
    var resboss2_2 = 0f
    var resbossy2_2 = 0f
    var down1 = 0f
    var down2 = 0f
    var down3 = 0f
    var down4 = 0f
    var down5 = 0f
    var lateral_movement = arrayOf<Float>(0f,0f,0f,0f,0f)
    var lateral_movement_boss = arrayOf<Float>(0f,0f)
    var dx_if_true = arrayOf<Boolean>(true,true,true,true,true)
    var dx_if_true_boss =  arrayOf<Boolean>(true,true)
    var count_boss = arrayOf<Int>(0,0)
    var up_if_true = arrayOf<Boolean>(false,false,false)
    var random_x = 10f //how go far along the x axe
    var change = 1 // to change the direction randomly in the boss

    var hearts = 3 // life of the player
    var life_boss = arrayOf<Int>(3,3)
    var bullet_available = 3
    var bullet_boss = arrayOf<Int>(3,3)
    var beat_boss1 = false

    var boss_x = arrayOf<Float>(0f,0f)
    var boss_y = arrayOf<Float>(0f,0f)
    var boss_visible = arrayOf<Boolean>(false,false,false)

    var ex_array = arrayOf<Boolean>(true,true,true,true,true)
    //var enemy_visible = arrayOf<Boolean>(false,false,false,false,false)

    var start:Boolean = true
    ///////// posizione per la collisione /////////
    var plane_x = 500f
    var plane_y = 1400f
    var enemy_position_x = arrayOf<Float>(0f,0f,0f,0f,0f)
    var enemy_position_y = arrayOf<Float>(0f,0f,0f,0f,0f)

    var InScreen1 = false // if bullet are still in the screen
    var InScreen2 = false
    var InScreen3 = false
    var return_to_game = false // to avoid shot bullet when click on the resume button

    var size = 2f
    var sizeA = 120f
    var sizeB = 30f
    var sizeBoss = 200f
    var sizeHeart = 50f
    var background : Bitmap
    lateinit var airplane : Bitmap
    lateinit var bullet : Bitmap
    var enemy_type1 : Bitmap
    var enemy_type2 : Bitmap
    var enemy_type3 : Bitmap
    var boss1 : Bitmap
    var boss2 : Bitmap
    var heart : Bitmap
    var explosion : Bitmap
    var pause_button : Bitmap
    var resume_button :Bitmap
    var quit_button :Bitmap

    var array_position = arrayOf<Float>(0f,0f,0f,0f,0f)
    var array_type = arrayOf<Int>(1,1,1,1,1)
    var enemy_visible = arrayOf<Boolean>(false, false,false,false,false)

    var bullet_position_x = arrayOf<Float>(1200f,1200f,1200f,1200f,1200f)
    var bullet_position_y = arrayOf<Float>(0f,0f,0f,0f,0f)

    ///// punti ottenuti nel gioco variabili /////
    var Score = 0
    var calendar = Calendar.getInstance()
    var current_time = 0
    var change_score = true
    var old_time = 0
    var Old_score = 0
    var id = ""
    var PAUSE = false
    var gameover = false


    init{
        var layout = R.drawable.background_sun2
        if (old_score != null) {
            Old_score = old_score
        }
        if (ID != null) {
            id = ID
        }
        //Log.i("prova",w.toString())
        if((w=="Overcast") or (w=="Partly cloudy")or(w=="Cloudy")){ // sfondo quando nuvoloso
            layout = R.drawable.rainybackground
        } else if((w=="Sunny")or(w=="Clear")) {//sfondo soleggiato
            layout = R.drawable.background_sun2
        } else if((w=="Patchy rain possible") or (w=="Light rain") or(w=="Patchy light rain") or(w=="Moderate rain at times") or (w=="Moderate rain") or(w=="Heavy rain at times")  or(w=="Heavy rain") or (w=="Light freezing rain") or (w=="Torrential rain shower") or(w=="Moderate or heavy rain with thunder") or(w=="Patchy light rain with thunder")){//sfondo piovoso
            layout = R.drawable.rainybackground
        }else if((w=="Heavy snow") or(w=="Patchy heavy snow")or(w=="Moderate or heavy snow with thunder")or(w=="Patchy light snow with thunder")){
            layout = R.drawable.snowybackground
        } else{//if found something we don t have
            layout = R.drawable.background_sun2
        }
        if (w != null) {
            weather= w
        }
        if (Password != null) {
            password= Password
        }
        if (Username != null) {
            username= Username
        }

        size*=160*resources.displayMetrics.density
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,  //use this since MyView implements the listener interface
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL)
        setOnTouchListener(this)
        background = ResourcesCompat.getDrawable(resources,layout,
            null)?.toBitmap(size.toInt(),size.toInt())!!
        //// airplane
        if(Color=="red"){
            airplane= ResourcesCompat.getDrawable(resources,R.drawable.airplane,
                null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        }else if(Color == "blue"){
            airplane= ResourcesCompat.getDrawable(resources,R.drawable.airplaneblue,
                null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        }else if(Color =="green"){
            airplane= ResourcesCompat.getDrawable(resources,R.drawable.airplanegreen,
                null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        }
        ///// bullet
        if(Bul == "normal"){
            bullet = ResourcesCompat.getDrawable(resources,R.drawable.bullet,
                null)?.toBitmap(sizeB.toInt(),sizeB.toInt())!!
        }else if(Bul == "laser"){
            bullet = ResourcesCompat.getDrawable(resources,R.drawable.laser_bullet2,
                null)?.toBitmap(sizeB.toInt(),sizeB.toInt())!!
        }
        //// enemies //////
        enemy_type1 = ResourcesCompat.getDrawable(resources,R.drawable.enemy1,
            null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        enemy_type2 = ResourcesCompat.getDrawable(resources,R.drawable.enemy2,
            null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        enemy_type3 = ResourcesCompat.getDrawable(resources,R.drawable.enemy3,
            null)?.toBitmap(sizeA.toInt(),sizeA.toInt())!!
        /// boss /////
        boss1 = ResourcesCompat.getDrawable(resources,R.drawable.boss1,
            null)?.toBitmap(sizeBoss.toInt(),sizeBoss.toInt())!!
        boss2 = ResourcesCompat.getDrawable(resources,R.drawable.boss_finale,
            null)?.toBitmap(sizeBoss.toInt(),sizeBoss.toInt())!!
        ///// hearts////
        heart = ResourcesCompat.getDrawable(resources,R.drawable.heart,
            null)?.toBitmap(sizeHeart.toInt(),sizeHeart.toInt())!!
        explosion = ResourcesCompat.getDrawable(resources,R.drawable.heart,
            null)?.toBitmap(sizeHeart.toInt(),sizeHeart.toInt())!!
        pause_button = ResourcesCompat.getDrawable(resources,R.drawable.pausebutton,
            null)?.toBitmap(sizeHeart.toInt(),sizeHeart.toInt())!!
        resume_button = ResourcesCompat.getDrawable(resources,R.drawable.resume_button,
            null)?.toBitmap(800,200)!!
        quit_button = ResourcesCompat.getDrawable(resources,R.drawable.quit_button,
            null)?.toBitmap(800,200)!!
        ///// first spawn of enemies /////
        GlobalScope.launch {
            spawn_enemy(0)
        }
        GlobalScope.launch {
            spawn_enemy(1)
        }
        GlobalScope.launch {
            spawn_enemy(2)
        }
    }

    /////////////////////////-----------------------//////////////////////////////////////////
    var array_enemy_type = arrayOf<Bitmap>(enemy_type1,enemy_type2,enemy_type3)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        background = Bitmap.createScaledBitmap(background, w, h, false)
        //airplane = Bitmap.createScaledBitmap(airplane, w/4, h/4, false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            if(start and !PAUSE){
                /*if(cont!=null){
                    Log.i("context",cont.toString())
                    game.gameover(cont)
                }*/
                ///// manage score ////
                calendar = Calendar.getInstance()
                current_time = calendar.get(Calendar.SECOND)
                if(current_time != old_time){
                    old_time = current_time
                    change_score = true
                }
                if((current_time %5== 0) and(change_score == true)){
                    Score += 5
                    change_score = false
                }
                Log.i("TIME",current_time.toString())
                drawBitmap(background,0f,0f,null)
                val message= "Score: $Score"
                val textPaint = Paint().also {
                    it.color = Color.parseColor("#000000")
                    it.strokeWidth = 100f
                    it.strokeMiter = 100f
                    it.textSize=70f
                }
                canvas.drawText(message,0,message.length,50f,80f,textPaint)
                ///////draw the airplane with constraint on the x axe /////////
                if((roll<-0.701f)){
                    withTranslation(-492f, 0f) {
                        drawBitmap(airplane, 500f, 1400f, null) }
                    plane_x = 0f
                }else if ((roll>0.701f)){
                    withTranslation(490f, 0f) {
                        drawBitmap(airplane, 500f, 1400f, null) }
                    plane_x = 990f
                }else{
                    withTranslation(roll *(500f/0.701f), 0f) {
                        drawBitmap(airplane, 500f, 1400f, null) }
                    plane_x = 500f + roll *(500f/0.701f)
                }
                ///////end draw airplane /////////
                //-------- draw hearts -------//
                if(hearts == 3){
                    drawBitmap(heart, 900f, 30f, null)
                    drawBitmap(heart, 950f, 30f, null)
                    drawBitmap(heart, 1000f, 30f, null)
                }else if(hearts==2){
                    drawBitmap(heart, 900f, 30f, null)
                    drawBitmap(heart, 950f, 30f, null)
                    // se vogliamo possiamo metter cuore vuoto
                }else{
                    drawBitmap(heart, 900f, 30f, null)
                }
                //------ end draw hearts ------//
                //------ draw bullet a disposizione -------//
                if(bullet_available == 3){
                    drawBitmap(bullet, 500f, 1720f, null)
                    drawBitmap(bullet, 550f, 1720f, null)
                    drawBitmap(bullet, 600f, 1720f, null)
                }else if(bullet_available==2){
                    drawBitmap(bullet, 500f, 1720f, null)
                    drawBitmap(bullet, 550f, 1720f, null)
                    // se vogliamo possiamo metter cuore vuoto
                }else if(bullet_available==1){
                    drawBitmap(bullet, 500f, 1720f, null)
                }
                //------ end draw bullet a disposizione -------//
                if(!enemy_visible[0] and ex_array[0]){
                    GlobalScope.launch {
                        spawn_enemy(0)
                    }
                }
                if(!enemy_visible[1] and ex_array[1]){
                    GlobalScope.launch {
                        spawn_enemy(1)
                    }
                }
                if(!enemy_visible[2] and ex_array[2]){
                    GlobalScope.launch {
                        spawn_enemy(2)
                    }
                }
                if(!enemy_visible[3] and ex_array[3] and (beat_boss1==true)){
                    GlobalScope.launch {
                        spawn_enemy(3)
                    }
                }
                /*if(!enemy_visible[4] and ex_array[4]){
                    GlobalScope.launch {
                        spawn_enemy(4)
                    }
                }*/
                //////////////////////// spawn bosses //////////////////
                if((Score >500)and(Score<1500) and (!boss_visible[0])){
                    GlobalScope.launch {
                        spawn_boss(0)
                    }
                }
                if((Score >3000) and (!boss_visible[1])){
                    GlobalScope.launch {
                        spawn_boss(1)
                    }
                }
                ///////////------------------ manage position of boss 1 -------------///////////////
                if(boss_visible[0]){
                    //// permette al boss di muoversi avanti e indietro per count volte lungo asse y
                    if((boss_y[0]>1000f) and(!up_if_true[0]) and (count_boss[0]<4)){
                        up_if_true[0] = true
                        count_boss[0] +=1
                    }
                    if((boss_y[0]<100f) and (up_if_true[0]) and (count_boss[0]<4)){
                        up_if_true[0] = false
                    }
                    ////// incremento lungo asse y
                    if(up_if_true[0]){ boss_y[0] -=5f}
                    if(!up_if_true[0]){ boss_y[0] +=5f}
                    //// controlla se andare a destra o sinisra entro certi limiti
                    if((lateral_movement_boss[0]+boss_x[0]>900f) and (dx_if_true_boss[0] == true)){
                        dx_if_true_boss[0] = false
                    }else if((lateral_movement_boss[0]+boss_x[0]<100f) and (dx_if_true_boss[0]==false)){
                        dx_if_true_boss[0] = true
                    }
                    /////// incremento lungo asse x a seconda se va a dx o sx
                    if(current_time%4==0){
                        random_x = ((5..15).random()).toFloat()
                        change =((0..2).random())
                        /*if(change == 0) {
                            dx_if_true_boss[0] = !dx_if_true_boss[0]
                        }*/
                    }
                    if(dx_if_true_boss[0]==true){lateral_movement_boss[0] =lateral_movement_boss[0]+random_x}
                    if(dx_if_true_boss[0]==false){lateral_movement_boss[0] =lateral_movement_boss[0]-random_x}

                    withTranslation (lateral_movement_boss[0],boss_y[0]) {
                        drawBitmap(boss1, boss_x[0], 0f, null)

                    }

                    //drawBitmap(boss1, boss_x[0], 0f, null)
                }
                /////////------------------- manage position of boss 2 ------------////////////
                if(boss_visible[1]){
                    //// permette al boss di muoversi avanti e indietro per count volte lungo asse y
                    if((boss_y[1]>1000f) and(!up_if_true[1]) and (count_boss[1]<4)){
                        up_if_true[1] = true
                        count_boss[1] +=1
                    }
                    if((boss_y[1]<100f) and (up_if_true[1]) and (count_boss[1]<4)){
                        up_if_true[1] = false
                    }
                    ////// incremento lungo asse y
                    if(up_if_true[1]){ boss_y[1] -=5f}
                    if(!up_if_true[1]){ boss_y[1] +=5f}
                    //// controlla se andare a destra o sinisra entro certi limiti
                    if((lateral_movement_boss[1]+boss_x[1]>900f) and (dx_if_true_boss[1] == true)){
                        dx_if_true_boss[1] = false
                    }else if((lateral_movement_boss[0]+boss_x[0]<100f) and (dx_if_true_boss[1]==false)){
                        dx_if_true_boss[1] = true
                    }
                    /////// incremento lungo asse x a seconda se va a dx o sx
                    if(current_time%4==0){
                        random_x = ((5..15).random()).toFloat()
                        change =((0..2).random())
                        /*if(change == 0) {
                            dx_if_true_boss[0] = !dx_if_true_boss[0]
                        }*/
                    }
                    if(dx_if_true_boss[1]==true){lateral_movement_boss[1] =lateral_movement_boss[1]+random_x}
                    if(dx_if_true_boss[1]==false){lateral_movement_boss[1] =lateral_movement_boss[1]-random_x}

                    withTranslation (lateral_movement_boss[1],boss_y[1]) {
                        drawBitmap(boss2, boss_x[1], 0f, null)

                    }
                }
                ////////// increase the value of the enemy translation //////////
                if(enemy_visible[0]) {
                    down1 += 10
                    withTranslation (0f,down1) {
                        drawBitmap(array_enemy_type[array_type[0]], array_position[0], 0f, null)

                    }
                }
                if(enemy_visible[1]) {
                    down2 += 10
                    withTranslation (0f,down2) {
                        drawBitmap(array_enemy_type[array_type[1]], array_position[1], 0f, null)

                    }
                }
                if(enemy_visible[2]) {
                    down3 += 10
                    withTranslation (0f,down3) {
                        drawBitmap(array_enemy_type[array_type[2]], array_position[2], 0f, null)

                    }
                }
                if(enemy_visible[3]) {
                    down4 += 10
                    withTranslation (0f,down4) {
                        drawBitmap(array_enemy_type[array_type[3]], array_position[3], 0f, null)

                    }
                }
                if(enemy_visible[4]) {
                    down5 += 10
                    if((lateral_movement[4]+array_position[4]>900f) and (dx_if_true[4] == true)){
                        dx_if_true[4] = false
                    }else if((lateral_movement[4]+array_position[4]<100f) and (dx_if_true[4]==false)){
                        dx_if_true[4] = true
                    }

                    if(dx_if_true[4]==true){lateral_movement[4] =lateral_movement[4]+20f}
                    if(dx_if_true[4]==false){lateral_movement[4] =lateral_movement[4]-20f}

                    withTranslation (lateral_movement[4],down5) {
                        drawBitmap(array_enemy_type[array_type[4]], array_position[4], 0f, null)

                    }
                }
                ///////////////////----------- MANAGE ENEMIES GO OUT OF THE SCREEN ----------////////////////////
                if(down1 > 1720f){  //// reset down variable to respawn the enemies
                    down1 = 0f
                    enemy_visible[0] = false
                    ex_array[0] = true
                    justcollide[0] = false
                }
                if(down2 > 1720f){  //// reset down variable to respawn the enemies
                    down2 = 0f
                    enemy_visible[1] = false
                    ex_array[1] = true
                    justcollide[1] = false
                }
                if(down3 > 1720f){  //// reset down variable to respawn the enemies
                    down3 = 0f
                    enemy_visible[2] = false
                    ex_array[2] = true
                    justcollide[2] = false
                }
                if(down4 > 1720f){  //// reset down variable to respawn the enemies
                    down4 = 0f
                    enemy_visible[3] = false
                    ex_array[3] = true
                    justcollide[3] = false
                }
                if(down5 > 1720f){  //// reset down variable to respawn the enemies
                    down5 = 0f
                    enemy_visible[4] = false
                    ex_array[4] = true
                    lateral_movement[4] = 0f
                    dx_if_true[4] = true
                    justcollide[4] = false
                }
                ///// boss /////
                if((boss_y[0] > 1720f)or(boss_y[0]<0f)){
                    boss_visible[0] = false
                    lateral_movement_boss[0] = 0f
                    dx_if_true_boss[0] = true
                }
                ///// boss finale
                if((boss_y[1] > 1720f) or(boss_y[0]<0f)){
                    boss_visible[1] = false
                    lateral_movement_boss[1] = 0f
                    dx_if_true_boss[1] = true
                }
                ////// cordinate of y of enemies for the collision /////////
                enemy_position_y[0]= down1
                enemy_position_y[1]= down2
                enemy_position_y[2]= down3
                enemy_position_y[3]= down4
                enemy_position_y[4]= down5
                ////// enemy bitmap ///////
                /////// enemy end //////////
                if(InScreen1) {
                    withTranslation(0f, up) {
                        up -= 20f
                        if (shot) {
                            drawBitmap(bullet, plane_x+50f, (1400f + up), null)
                            res = plane_x+50f
                            shot = false
                            bullet_position_x[0] = res
                            bullet_position_y[0] = (1400f+up)
                        } else {
                            drawBitmap(bullet, res, (1400f + up), null)
                            if((up)<-1400f){
                                InScreen1 = false
                                up = 0f
                                bullet_available +=1
                                just_shot_bullet[0] = false
                            }
                            bullet_position_x[0] = (res)
                            bullet_position_y[0] = (1400f + up)
                        }
                    }
                }
                if(InScreen2) {
                    withTranslation(0f, up2) {
                        up2 -= 20f
                        if (shot2) {
                            drawBitmap(bullet, plane_x+50f, (1400f + up2), null)
                            res2 =plane_x+50f
                            shot2 = false
                            bullet_position_x[1] =res2
                            bullet_position_y[1] = (1400f+up2)
                        } else {
                            drawBitmap(bullet, res2, (1400f + up2), null)
                            if((up2)<-1400f){
                                InScreen2 = false
                                up2 = 0f
                                bullet_available +=1
                                just_shot_bullet[1] = false
                            }
                            bullet_position_x[1] = (res2)
                            bullet_position_y[1] = (1400f+up2)
                        }
                    }
                }
                if(InScreen3){
                    withTranslation(0f, up3) {
                        up3 -= 20f
                        if (shot3) {
                            drawBitmap(bullet, plane_x+50f, (1400f + up3), null)
                            res3 = plane_x+50f
                            shot3 = false
                            bullet_position_x[2] = res3
                            bullet_position_y[2] = (1400f+up3)
                        } else {
                            drawBitmap(bullet, res3, (1400f + up3), null)
                            //Log.i("ROLL",(1400f+up3).toString())
                            if((up3)<-1400f){
                                //Log.i("ROLL","sta usando il terzo bullet")
                                InScreen3 = false
                                up3 = 0f
                                bullet_available +=1
                                just_shot_bullet[2] = false
                            }
                            bullet_position_x[2] = (res3)
                            bullet_position_y[2] = (1400f+up3)
                        }
                    }
                }
                ///////// boss1 bullet /////////////
                if(boss_visible[0]){
                    withTranslation(0f, upboss[0]) {
                        upboss[0]+= 10f
                        if (shotboss[0]) {
                            drawBitmap(bullet, lateral_movement_boss[0]+boss_x[0]+60f, (upboss[0]+boss_y[0]+120f), null)
                            resboss = lateral_movement_boss[0]+boss_x[0]+60f
                            resbossy = (upboss[0]+boss_y[0]+120f)
                            shotboss[0] = false
                            boss_bullet_position_x[0] = resboss
                            boss_bullet_position_y[0] = (resbossy)
                        } else {
                            drawBitmap(bullet, resboss, (resbossy+upboss[0]), null)
                            //Log.i("ROLL",(1400f+up3).toString())
                            if((upboss[0])>1400f){
                                //Log.i("ROLL","sta usando il terzo bullet")
                                upboss[0] = 0f
                                shotboss[0]= true
                                just_shot = false
                            }
                            boss_bullet_position_x[0] = (resboss)
                            boss_bullet_position_y[0] = (upboss[0]+resbossy)
                        }
                    }
                }
                ///////// boss2 first bullet /////////////
                if(boss_visible[1]){
                    withTranslation(0f, upboss[1]) {
                        upboss[1]+= 10f
                        if (shotboss[1]) {
                            drawBitmap(bullet, lateral_movement_boss[1]+boss_x[1]+60f, (upboss[1]+boss_y[1]+120f), null)
                            resboss2_1 = lateral_movement_boss[1]+boss_x[1]+60f
                            resbossy2_1 = (upboss[1]+boss_y[1]+120f)
                            shotboss[1] = false
                            boss_bullet_position_x[1] = resboss2_1
                            boss_bullet_position_y[1] = (resbossy2_1)
                        } else {
                            drawBitmap(bullet, resboss2_1, (resbossy2_1+upboss[1]), null)
                            //Log.i("ROLL",(1400f+up3).toString())
                            if((upboss[1])>1400f){
                                //Log.i("ROLL","sta usando il terzo bullet")
                                upboss[1] = 0f
                                shotboss[1]= true
                                just_shot = false
                            }
                            boss_bullet_position_x[1] = (resboss2_1)
                            boss_bullet_position_y[1] = (upboss[1]+resbossy2_1)
                        }
                    }
                }

                ///////// boss2 second bullet /////////////
                if(boss_visible[1]){
                    withTranslation(0f, upboss[1]) {
                        upboss[1]+= 10f
                        if (shotboss[1]) {
                            drawBitmap(bullet, lateral_movement_boss[1]+boss_x[1]+60f, (upboss[1]+boss_y[1]+120f), null)
                            resboss2_2 = lateral_movement_boss[1]+boss_x[1]+60f
                            resbossy2_2 = (upboss[1]+boss_y[1]+120f)
                            shotboss[1] = false
                            boss_bullet_position_x[1] = resboss2_2
                            boss_bullet_position_y[1] = (resbossy2_2)
                        }else {
                            drawBitmap(bullet, resboss2_2, (resbossy2_2+upboss[1]), null)
                            //Log.i("ROLL",(1400f+up3).toString())
                            if((upboss[1])>1400f){
                                //Log.i("ROLL","sta usando il terzo bullet")
                                upboss[1] = 0f
                                shotboss[1]= true
                                just_shot = false
                            }
                            boss_bullet_position_x[1] = (resboss2_2)
                            boss_bullet_position_y[1] = (upboss[1]+resbossy2_2)
                        }
                    }
                }


                ////////////////  ------------- MANAGE COLLISION PLANE WITH ENEMIES AND BOSS BULLET  ----------------- ////////////////
                if( (array_position[0]>= plane_x-110f) and (array_position[0]<=plane_x+110f)and(enemy_position_y[0] <= plane_y+235f)and(enemy_position_y[0] >= plane_y-115f)){
                    if(!justcollide[0]) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            justcollide[0] = true
                        }
                    }
                }
                if( (array_position[1]>= plane_x-110f)and(array_position[1]<=plane_x+110f)and(enemy_position_y[1] <= plane_y+235f)and(enemy_position_y[1] >= plane_y-115f)){
                    if(!justcollide[1]) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            justcollide[1] = true
                        }
                    }
                }
                if( (array_position[2]>= plane_x-110f)and(array_position[2]<=plane_x+110f)and(enemy_position_y[2] <= plane_y+235f)and(enemy_position_y[2] >= plane_y-115f)){
                    if(!justcollide[2]) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            justcollide[2] = true
                        }
                    }
                }
                if( (array_position[3]>= plane_x-110f)and(array_position[3]<=plane_x+110f)and(enemy_position_y[3] <= plane_y+235f)and(enemy_position_y[3] >= plane_y-115f)){
                    if(!justcollide[3]) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            justcollide[3] = true
                        }
                    }
                }
                if( (lateral_movement[4]+array_position[4]>= plane_x-15f)and(lateral_movement[4]+array_position[4]<=plane_x+15f)and(enemy_position_y[4] == plane_y)){
                    if(!justcollide[4]) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            justcollide[4] = true
                        }
                    }
                }
                ////////////boss 1 bullet
                if((boss_bullet_position_x[0] >=plane_x -25f) and (boss_bullet_position_x[0] <=plane_x +145f)and
                    (boss_bullet_position_y[0] <=plane_y +145f) and(boss_bullet_position_y[0] >=plane_y -25f)){
                    if(just_shot==false) {////// to avoid multiple collision of same bullet
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot=true
                        }
                    }
                }
                ///////boss 2 bullets
                if((boss_bullet_position_x[1] >=plane_x -25f) and (boss_bullet_position_x[1] <=plane_x +145f)and
                    (boss_bullet_position_y[1] <=plane_y +145f) and(boss_bullet_position_y[1] >=plane_y -25f)){
                    if(just_shot==false) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot=true
                        }
                    }
                }
                if((boss_bullet_position_x[2] >=plane_x -25f) and (boss_bullet_position_x[2] <=plane_x +145f)and
                    (boss_bullet_position_y[2] <=plane_y +145f) and(boss_bullet_position_y[2] >=plane_y -25f)){
                    if(just_shot==false) {
                        if (hearts == 0) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot=true
                        }
                    }
                }
                ///////////////////////// ---------------------------------- ////////////////////////////////

                /////////////////////////////// -------------- MANAGE COLLISION OF OUR PLANE BULLETS   ----------------- ///////////////////////////
                ///////////////bullet 1
                if((bullet_position_x[0]>=array_position[0]-35f)and(bullet_position_x[0]<=array_position[0]+115f)and(bullet_position_y[0]<=enemy_position_y[0]+120f)and(bullet_position_y[0]>=enemy_position_y[0])){
                    if(!just_shot_bullet[0]){
                        Score += 20
                        down1 = 0f
                        enemy_visible[0] = false
                        ex_array[0] = true
                        drawBitmap(explosion,array_position[0],enemy_position_y[0],null)/// gif for the explosion
                        just_shot_bullet[0] = true
                    }
                }
                if((bullet_position_x[0]>=array_position[1]-35f)and(bullet_position_x[0]<=array_position[1]+115f)and(bullet_position_y[0]<=enemy_position_y[1]+120f)and(bullet_position_y[0]>=enemy_position_y[1])){
                    if(!just_shot_bullet[0]) {
                        Score += 20
                        down2 = 0f
                        enemy_visible[1] = false
                        ex_array[1] = true
                        drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                        just_shot_bullet[0] = true
                    }
                }
                if((bullet_position_x[0]>=array_position[2]-35f)and(bullet_position_x[0]<=array_position[2]+115f)and(bullet_position_y[0]<=enemy_position_y[2]+120f)and(bullet_position_y[0]>=enemy_position_y[2])){
                    if(!just_shot_bullet[0]) {
                        Score += 20
                        down3 = 0f
                        enemy_visible[2] = false
                        ex_array[2] = true
                        drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                        just_shot_bullet[0] = true
                    }
                }
                if((bullet_position_x[0]>=array_position[3]-35f)and(bullet_position_x[0]<=array_position[3]+115f)and(bullet_position_y[0]<=enemy_position_y[3]+120f)and(bullet_position_y[0]>=enemy_position_y[3])){
                    if(!just_shot_bullet[0]) {
                        Score += 20
                        down4 = 0f
                        enemy_visible[3] = false
                        ex_array[3] = true
                        drawBitmap(explosion,array_position[3],enemy_position_y[3],null)
                        just_shot_bullet[0] = true
                    }
                }
                if((bullet_position_x[0]>=lateral_movement[4]+array_position[4]-35f)and(bullet_position_x[0]<=lateral_movement[4]+array_position[4]+115f)and(bullet_position_y[0]<=enemy_position_y[4]+120f)and(bullet_position_y[0]>=enemy_position_y[4])) {
                    if(!just_shot_bullet[0]) {
                        Score += 20
                        down5 = 0f
                        enemy_visible[4] = false
                        ex_array[4] = true
                        drawBitmap(explosion,array_position[4],enemy_position_y[4],null)
                        just_shot_bullet[0] = true
                    }
                }
                //////////////bullet 2 //////////////////////////
                if((bullet_position_x[1]>=array_position[0]-35f)and(bullet_position_x[1]<=array_position[0]+115f)and(bullet_position_y[1]<=enemy_position_y[0]+120f)and(bullet_position_y[1]>=enemy_position_y[0])){
                    if(!just_shot_bullet[1]) {
                        Score += 20
                        down1 = 0f
                        enemy_visible[0] = false
                        ex_array[0] = true
                        drawBitmap(explosion,array_position[0],enemy_position_y[0],null)
                        just_shot_bullet[1] = true
                    }
                }
                if((bullet_position_x[1]>=array_position[1]-35f)and(bullet_position_x[1]<=array_position[1]+115f)and(bullet_position_y[1]<=enemy_position_y[1]+120f)and(bullet_position_y[1]>=enemy_position_y[1])){
                    if(!just_shot_bullet[1]) {
                        Score += 20
                        down2 = 0f
                        enemy_visible[1] = false
                        ex_array[1] = true
                        drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                        just_shot_bullet[1] = true
                    }
                }
                if((bullet_position_x[1]>=array_position[2]-35f)and(bullet_position_x[1]<=array_position[2]+115f)and(bullet_position_y[1]<=enemy_position_y[2]+120f)and(bullet_position_y[1]>=enemy_position_y[2])){
                    if(!just_shot_bullet[1]) {
                        Score += 20
                        down3 = 0f
                        enemy_visible[2] = false
                        ex_array[2] = true
                        drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                        just_shot_bullet[1] = true
                    }
                }
                if((bullet_position_x[1]>=array_position[3]-35f)and(bullet_position_x[1]<=array_position[3]+115f)and(bullet_position_y[1]<=enemy_position_y[3]+120f)and(bullet_position_y[1]>=enemy_position_y[3])){
                    if(!just_shot_bullet[1]) {
                        Score += 20
                        down4 = 0f
                        enemy_visible[3] = false
                        ex_array[3] = true
                        drawBitmap(explosion,array_position[3],enemy_position_y[3],null)
                        just_shot_bullet[1] = true
                    }
                }
                if((bullet_position_x[1]>=lateral_movement[4]+array_position[4]-35f)and(bullet_position_x[1]<=lateral_movement[4]+array_position[4]+115f)and(bullet_position_y[1]<=enemy_position_y[4]+120f)and(bullet_position_y[1]>=enemy_position_y[4])) {
                    if(!just_shot_bullet[1]) {
                        Score += 20
                        down5 = 0f
                        enemy_visible[4] = false
                        ex_array[4] = true
                        drawBitmap(explosion,array_position[4],enemy_position_y[4],null)
                        just_shot_bullet[1] = true
                    }
                }
                //////////////////////bullet 3 ////////////////////////////
                if((bullet_position_x[2]>=array_position[0]-35f)and(bullet_position_x[2]<=array_position[0]+115f)and(bullet_position_y[2]<=enemy_position_y[0]+120f)and(bullet_position_y[2]>=enemy_position_y[0])){
                    if(!just_shot_bullet[2]) {
                        Score += 20
                        down1 = 0f
                        enemy_visible[0] = false
                        ex_array[0] = true
                        drawBitmap(explosion,array_position[0],enemy_position_y[0],null)
                        just_shot_bullet[2] = true
                    }
                }
                if((bullet_position_x[2]>=array_position[1]-35f)and(bullet_position_x[2]<=array_position[1]+115f)and(bullet_position_y[2]<=enemy_position_y[1]+120f)and(bullet_position_y[2]>=enemy_position_y[1])){
                    if(!just_shot_bullet[2]) {
                        Score += 20
                        down2 = 0f
                        enemy_visible[1] = false
                        ex_array[1] = true
                        drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                        just_shot_bullet[2] = true
                    }
                }
                if((bullet_position_x[2]>=array_position[2]-35f)and(bullet_position_x[2]<=array_position[2]+115f)and(bullet_position_y[2]<=enemy_position_y[2]+120f)and(bullet_position_y[2]>=enemy_position_y[2])){
                    if(!just_shot_bullet[2]) {
                        Score += 20
                        down3 = 0f
                        enemy_visible[2] = false
                        ex_array[2] = true
                        drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                        just_shot_bullet[2] = true
                    }
                }
                if((bullet_position_x[2]>=array_position[3]-35f)and(bullet_position_x[2]<=array_position[3]+115f)and(bullet_position_y[2]<=enemy_position_y[3]+120f)and(bullet_position_y[2]>=enemy_position_y[3])){
                    if(!just_shot_bullet[2]) {
                        Score += 20
                        down4 = 0f
                        enemy_visible[3] = false
                        ex_array[3] = true
                        drawBitmap(explosion,array_position[3],enemy_position_y[3],null)
                        just_shot_bullet[2] = true
                    }
                }
                if((bullet_position_x[2]>=lateral_movement[4]+array_position[4]-35f)and(bullet_position_x[2]<=lateral_movement[4]+array_position[4]+115f)and(bullet_position_y[2]<=enemy_position_y[4]+120f)and(bullet_position_y[2]>=enemy_position_y[4])) {
                    if(!just_shot_bullet[2]) {
                        Score += 20
                        down5 = 0f
                        enemy_visible[4] = false
                        ex_array[4] = true
                        drawBitmap(explosion,array_position[4],enemy_position_y[4],null)
                        just_shot_bullet[2] = true
                    }
                }
                //////////******** MANAGE COLLISION OUR BULLETS TO BOSS 1 *******////////////////
                if((bullet_position_x[0]>=boss_x[0]-35f)and(bullet_position_x[0]<=boss_x[0]+195f)and(bullet_position_y[0]<=boss_y[0]+200f)and(bullet_position_y[0]>=boss_y[0])) {
                    if(!just_shot_bullet[0]){
                        just_shot_bullet[0] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[0],boss_y[0],null)
                            beat_boss1 = true
                        }
                    }
                }
                if((bullet_position_x[1]>=boss_x[1]-35f)and(bullet_position_x[1]<=boss_x[0]+195f)and(bullet_position_y[1]<=boss_y[0]+200f)and(bullet_position_y[1]>=boss_y[0])) {
                    if(!just_shot_bullet[1]){
                        just_shot_bullet[1] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[0],boss_y[0],null)
                            beat_boss1 = true
                        }
                    }
                }
                if((bullet_position_x[2]>=boss_x[0]-35f)and(bullet_position_x[2]<=boss_x[0]+195f)and(bullet_position_y[2]<=boss_y[0]+200f)and(bullet_position_y[2]>=boss_y[0])) {
                    if(!just_shot_bullet[2]){
                        just_shot_bullet[2] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[0],boss_y[0],null)
                            beat_boss1 = true
                        }
                    }
                }
                //////////******** OUR BULLET HIT BOSS 2 *******////////////////
                if((bullet_position_x[0]>=boss_x[1]-35f)and(bullet_position_x[0]<=boss_x[1]+195f)and(bullet_position_y[0]<=boss_y[1]+200f)and(bullet_position_y[0]>=boss_y[1])) {
                    if(!just_shot_bullet[0]){
                        just_shot_bullet[0] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[1],boss_y[1],null)
                        }
                    }
                }
                if((bullet_position_x[1]>=boss_x[1]-35f)and(bullet_position_x[1]<=boss_x[1]+195f)and(bullet_position_y[1]<=boss_y[1]+200f)and(bullet_position_y[1]>=boss_y[1])) {
                    if(!just_shot_bullet[1]){
                        just_shot_bullet[1] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[1],boss_y[1],null)
                        }
                    }
                }
                if((bullet_position_x[2]>=boss_x[1]-35f)and(bullet_position_x[2]<=boss_x[1]+195f)and(bullet_position_y[2]<=boss_y[1]+200f)and(bullet_position_y[2]>=boss_y[1])) {
                    if(!just_shot_bullet[2]){
                        just_shot_bullet[2] = true
                        if(life_boss[0]>1){
                            life_boss[0] -=1
                        }else{
                            life_boss[0] -= 1
                            boss_visible[0]= false
                            Score += 200
                            drawBitmap(explosion,boss_x[1],boss_y[1],null)
                        }
                    }
                }
                //////////////// end collision bullet ///////////////
            }else if(PAUSE and !start){
                val message="GAME PAUSED"
                val textPaint = Paint().also {
                    it.color = Color.parseColor("#000000")
                    it.strokeWidth = 100f
                    it.textSize=130f
                }
                drawBitmap(background,0f,0f,null)
                canvas.drawBitmap(resume_button,130f,1000f,null)
                canvas.drawBitmap(quit_button,130f,1300f,null)
                drawText(message,100f,300f,textPaint)
            }else if(!PAUSE and !start) {
                ///////// gameover ////////////
                gameover = true
                if(Score >Old_score){
                    //Connecting to Firebase Database
                    val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

                    val referenceOldScore = database.getReference("Users/$id/Score")
                    referenceOldScore.setValue(Score.toString())
                    Old_score = Score
                }
                plane_x = 500f
                Score = 0
                hearts = 3
                val intent = Intent(context, GameoverActivity::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Password",password)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",Old_score)
                startActivity(context, intent, null)
            }
            if(!gameover){
                invalidate()
            }
        }
    }

    //Implementation of Event Listener Interface
    override fun onSensorChanged(p0: SensorEvent?) {

        mLastRotationVector = p0?.values?.clone()!! //Get last rotation vector
        //Compute the rotation matrix from the rotation vector
        SensorManager.getRotationMatrixFromVector(mRotationMatrix,mLastRotationVector)

        //Compute the roll angle
        roll = a*roll+(1-a)* atan2(-(mRotationMatrix[6]),mRotationMatrix[8])
        //Log.i("ROLL",roll.toString())
        if(!gameover ){
            invalidate()
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//
    }

    override fun onFlushCompleted(p0: Sensor?) {
        //
    }

    ////////////////// ONTOUCH /////////////////////
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                //Log.i("BUTTON",event.getRawX().toString())
                ///when touch the pause button start the onPause of the activity
                if((!PAUSE) and((event.getX()>970f)and (event.getY()>1700f))){
                    PAUSE = true
                    start = false
                }else if(PAUSE and ((event.getX()>130f)and (event.getX()<930f)and (event.getY()>1000f)and (event.getY()<1200f))){
                    start = true
                    PAUSE = false
                    return_to_game = true
                }else if(PAUSE and((event.getX()>130f)and (event.getX()<930f)and (event.getY()>1300f)and (event.getY()<1500f))){
                    PAUSE = false
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("Username",username)
                    intent.putExtra("Password",password)
                    intent.putExtra("ID",id)
                    intent.putExtra("Weather",weather)
                    intent.putExtra("Score",Old_score)
                    startActivity(context, intent, null)
                }
                if(start and !return_to_game){
                    if (!InScreen1 or !InScreen2 or !InScreen3) {
                        if(InScreen3 == false ) {
                            shot3 = true
                            InScreen3 = true
                            bullet_available -=1
                        }else if(InScreen2 == false) {
                            shot2 = true
                            InScreen2 = true
                            bullet_available -=1
                        }else if(InScreen1 == false){
                            shot = true
                            InScreen1 = true
                            bullet_available -=1
                        }
                    }
                }
                else if (!start and !PAUSE){
                    start = true
                    down1 = 0f
                    down2 = 0f
                    down3 = 0f
                    down4 = 0f
                    down5 = 0f
                    roll = 0f
                    bullet_available = 3
                    for(i in (0..4)){
                        array_position[i]= 0f
                    }
                    up = 0f
                }

                if(!gameover){
                    return_to_game = false
                    invalidate()
                }
            }
        }
        return true
    }
    ///////////////////// ONTOUCH FINISH ///////////////////
    suspend fun spawn_enemy(n:Int){
        ex_array[n]= false
        var delay_enemy = (3000L..8000L).random()
        delay(delay_enemy)
        //Log.i("Prova", "si la fa")
        var type = (0..2).random()
        var position = ((60..940).random())
        //Log.i("Prova", position.toString())
        for (i in (0..4)) {
            if (!enemy_visible[i]) {
                enemy_visible[i] = true
                array_type[i] = type
                array_position[i] = position.toFloat()
                break
            }
        }
    }

    fun spawn_boss(n:Int){
        //var delay_enemy = (3000L..8000L).random()
        //delay(delay_enemy)
        //var type = (0..2).random()
        var position = ((100..990).random())
        //Log.i("Prova", position.toString())
        boss_x[n] = position.toFloat()
        if(!boss_visible[n]){
            boss_visible[n] = true
            boss_x[n] = position.toFloat()
            shotboss[n]= true
        }
    }
}