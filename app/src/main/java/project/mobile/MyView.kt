package project.mobile

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withTranslation
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.atan2


class MyView(context: Context?, weat:String?, Color :String?, Bul :String?, Logged : Boolean?, packagename: String) : View(context), View.OnTouchListener, SensorEventListener2 {

    val a = 0.65f //Low-pass filter parameter, higher is smoother

    var mLastRotationVector = FloatArray(3) //The last value of the rotation vector
    var mRotationMatrix = FloatArray(9)
    var roll = 0f
    var oldRoll = 0f

    //Bullet positions with respect to y axis
    var up = arrayOf<Float>(0f, 0f, 0f)

    var upboss = arrayOf<Float>(0f,0f)
    var shotboss = arrayOf<Boolean>(false,false)
    var boss_bullet_position_x=arrayOf<Float>(0f,0f,0f)
    var boss_bullet_position_y=arrayOf<Float>(0f,0f,0f)
    //var just_shot = false ///to avoid multiple collision of single bullet of the boss
    var justcollide = arrayOf<Boolean>(false,false,false,false,false) // to avoid multiple collision of single enemy
    var just_shot_bullet =  arrayOf<Boolean>(false,false,false) // to avoid multiple collision of single bullet of our airplane
    var just_shot_bullet_boss = arrayOf<Boolean>(false,false,false) // to avoid multiple collision of single bullet of our airplane on the boss

    //Is the bullet shot in this moment
    var is_shot = arrayOf(false, false, false)

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
    var increment_velocity = 0 /// to augment velocity of enemy after tot points
    var augment_score =  arrayOf<Boolean>(false,false,false,false)
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
    var boss1_spawned = false
    var boss2_spawned = false
    var beat_boss1 = false  /// allow to spawn a plus enemy(che si muove lateralmente) after have beaten the boss 1

    var boss_x = arrayOf<Float>(0f,0f)
    var boss_y = arrayOf<Float>(0f,0f)
    var boss_visible = arrayOf<Boolean>(false,false,false)

    var ex_array = arrayOf<Boolean>(true,true,true,true,true)

    var start:Boolean = true
    ///////// posizione per la collisione /////////
    var plane_x = 500f
    var plane_y = 1400f

    var return_to_game = false // to avoid shot bullet when click on the resume button

    var size = 2f
    var sizeA = 150f
    var widthBullet = 20f
    var heighBullet = 40f
    var sizeBoss = 200f
    var sizeHeart = 50f
    var background : Bitmap
    lateinit var airplane : Bitmap
    lateinit var airplaneLeft : Bitmap
    lateinit var airplaneRight : Bitmap
    lateinit var bullet : Bitmap
    lateinit var bulletAvailable : Bitmap
    var enemy_type1 : Bitmap
    var enemy_type2 : Bitmap
    var enemy_type3 : Bitmap
    var boss1 : Bitmap
    var boss2 : Bitmap
    var heart : Bitmap
    var skull : Bitmap
    var pause_button : Bitmap
    var resume_button : Bitmap
    var quit_button : Bitmap
    var pause_label : Bitmap

    var array_position = arrayOf<Float>(0f,0f,0f,0f,0f)
    var enemy_position_y = arrayOf<Float>(0f,0f,0f,0f,0f)
    var array_type = arrayOf<Int>(1,1,1,1,1)
    var enemy_visible = arrayOf<Boolean>(false, false,false,false,false)

    var bullet_position_x = arrayOf<Float>(1200f,1200f,1200f,1200f,1200f)
    var bullet_position_y = arrayOf<Float>(0f,0f,0f,0f,0f)

    //Let the bullet invisible after collision
    var is_visible_bul = arrayOf(false, false, false)

    ///// punti ottenuti nel gioco variabili /////
    var Score = 0
    var calendar = Calendar.getInstance()
    var current_time = 0
    var change_score = true
    var old_time = 0
    var old_score = current_score
    var id = ""
    var PAUSE = false
    var gameover = false
    
    var numKills = 0        //Variable that counts the number of kills

    var col = ""
    var bul = ""
    var logged = false

    ///////////////ESPLOSIONE
    var retreiver: MediaMetadataRetriever
    var bitmapVideo = ArrayList<Bitmap>()
    var bitmapVideoBoss = ArrayList<Bitmap>()


    //EXPLOSION BULLET 1
    //Array that says for the bullet 1 if one of the 5 planes is exploding
    var isExpBul1 = arrayOf<Boolean>(false, false, false, false, false)

    //Array that contains the frame to show for the explosions with bullet 1
    var expFrame1 = arrayOf<Int>(0, 0, 0, 0, 0)

    //Array that contains the positions of the explosions with bullet 1
    var expPos1 = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))


    //EXPLOSION BULLET 2
    //Array that says for the bullet 2 if one of the 5 planes is exploding
    var isExpBul2 = arrayOf<Boolean>(false, false, false, false, false)

    //Array that contains the frame to show for the explosions with bullet 2
    var expFrame2 = arrayOf<Int>(0, 0, 0, 0, 0)

    //Array that contains the positions of the explosions with bullet 2
    var expPos2 = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))

    //EXPLOSION BULLET 3
    //Array that says for the bullet 3 if one of the 5 planes is exploding
    var isExpBul3 = arrayOf<Boolean>(false, false, false, false, false)

    //Array that contains the frame to show for the explosions with bullet 3
    var expFrame3 = arrayOf<Int>(0, 0, 0, 0, 0)

    //Array that contains the positions of the explosions with bullet 3
    var expPos3 = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))
    /////////////
    //EXPLOSION BULLET 1 BOSS
    //Array that says for the bullet  if one of the 2 boss is exploding
    var isExpBul1_Boss = arrayOf<Boolean>(false, false)

    //Array that contains the frame to show for the explosions with bullet
    var expFrame1_Boss = arrayOf<Int>(0, 0)

    //Array that contains the positions of the explosions with bullet 1
    var expPos1_Boss = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))

    //EXPLOSION BULLET 2 BOSS
    //Array that says for the bullet  if one of the 2 boss is exploding
    var isExpBul2_Boss = arrayOf<Boolean>(false, false)

    //Array that contains the frame to show for the explosions with bullet
    var expFrame2_Boss = arrayOf<Int>(0, 0)

    //Array that contains the positions of the explosions with bullet 1
    var expPos2_Boss = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))

    //EXPLOSION BULLET 3 BOSS
    //Array that says for the bullet  if one of the 2 boss is exploding
    var isExpBul3_Boss = arrayOf<Boolean>(false, false)

    //Array that contains the frame to show for the explosions with bullet
    var expFrame3_Boss = arrayOf<Int>(0, 0)

    //Array that contains the positions of the explosions with bullet 1
    var expPos3_Boss = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))

    /////////////*************************////////////////////

    init {
        ////////////////ESPLOSIONE
        retreiver = MediaMetadataRetriever()

        retreiver.setDataSource(
            context, Uri.parse(
                "android.resource://"
                        + packagename + "/" + R.raw.explosion
            )
        )

        var i = 0

        while (i < 1000000) {
            var bitmap =
                retreiver.getFrameAtTime(i.toLong(), MediaMetadataRetriever.OPTION_CLOSEST)!!

            //Add transparent background
            val width: Int = bitmap.getWidth()
            val height: Int = bitmap.getHeight()
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, 1 * width, 0, 0, width, height)
            for (x in pixels.indices) {
                if (pixels[x] == -1) pixels[x] = 0
            }
            bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)


            bitmapVideo.add(Bitmap.createScaledBitmap(bitmap, 150, 150, true))
            bitmapVideoBoss.add(Bitmap.createScaledBitmap(bitmap, 200, 200, true))
            i += 100000
        }

        /////////////7
        if (Color != null) {
            col = Color
        }
        if (Bul != null) {
            bul = Bul
        }
        if (Logged != null) {
            logged = Logged
        }

        var layout = R.drawable.background_sun2

        Log.i("Weather", weat.toString())
        if ((weat == "Overcast") or (weat == "Partly cloudy") or (weat == "Cloudy")) { // Background when cloudy
            layout = R.drawable.rainybackground
        } else if ((weat == "Sunny") or (weat == "Clear")) {//sfondo soleggiato
            layout = R.drawable.background_sun2
        } else if ((weat == "Patchy rain possible") or (weat == "Light rain") or (weat == "Patchy light rain") or (weat == "Moderate rain at times") or (weat == "Moderate rain") or (weat == "Heavy rain at times") or (weat == "Heavy rain") or (weat == "Light freezing rain") or (weat == "Torrential rain shower") or (weat == "Moderate or heavy rain with thunder") or (weat == "Patchy light rain with thunder")) {//sfondo piovoso
            layout = R.drawable.rainybackground
        } else if ((weat == "Heavy snow") or (weat == "Patchy heavy snow") or (weat == "Moderate or heavy snow with thunder") or (weat == "Patchy light snow with thunder")) {
            layout = R.drawable.snowybackground
        } else {//if found something we don t have
            layout = R.drawable.background_sun2
        }

        size *= 160 * resources.displayMetrics.density
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,  //use this since MyView implements the listener interface
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        setOnTouchListener(this)
        background = ResourcesCompat.getDrawable(
            resources, layout,
            null
        )?.toBitmap(size.toInt(), size.toInt())!!
        //// airplane
        if (Color == "red") {
            airplane = ResourcesCompat.getDrawable(
                resources, R.drawable.airplane,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!

            airplaneLeft = ResourcesCompat.getDrawable(
                resources, R.drawable.airplaneleft,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!

            airplaneRight = ResourcesCompat.getDrawable(
                resources, R.drawable.airplaneright,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!
        } else if (Color == "blue") {
            airplane = ResourcesCompat.getDrawable(
                resources, R.drawable.airplaneblue,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!
            airplaneLeft = ResourcesCompat.getDrawable(
                resources, R.drawable.airplaneblueleft,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!

            airplaneRight = ResourcesCompat.getDrawable(
                resources, R.drawable.airplaneblueright,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!
        } else if (Color == "green") {
            airplane = ResourcesCompat.getDrawable(
                resources, R.drawable.airplanegreen,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!

            airplaneLeft = ResourcesCompat.getDrawable(
                resources, R.drawable.airplanegreenleft,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!

            airplaneRight = ResourcesCompat.getDrawable(
                resources, R.drawable.airplanegreenright,
                null
            )?.toBitmap(sizeA.toInt(), sizeA.toInt())!!
        }
        ///// bullet
        if (Bul == "normal") {
            bullet = ResourcesCompat.getDrawable(
                resources, R.drawable.bullet,
                null
            )?.toBitmap(widthBullet.toInt(), heighBullet.toInt())!!
        } else if (Bul == "laser") {
            bullet = ResourcesCompat.getDrawable(
                resources, R.drawable.laserbullet,
                null
            )?.toBitmap(widthBullet.toInt(), heighBullet.toInt())!!
        }

        if (Bul == "normal"){
            //Bullet available
            bulletAvailable = ResourcesCompat.getDrawable(
                resources, R.drawable.bullet,
                null
            )?.toBitmap(40, 80)!!
        } else {
            //Bullet available
            bulletAvailable = ResourcesCompat.getDrawable(
                resources, R.drawable.laserbullet,
                null
                )?.toBitmap(40, 80)!!
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
        skull = ResourcesCompat.getDrawable(resources,R.drawable.skull,
            null)?.toBitmap(120,120)!!
        pause_button = ResourcesCompat.getDrawable(resources,R.drawable.pausebutton,
            null)?.toBitmap(200, 200)!!
        resume_button = ResourcesCompat.getDrawable(resources,R.drawable.resume_button,
            null)?.toBitmap(800,200)!!
        quit_button = ResourcesCompat.getDrawable(resources,R.drawable.quit_button,
            null)?.toBitmap(800,200)!!
        pause_label = ResourcesCompat.getDrawable(resources,R.drawable.pauselabel,
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
                if((current_time %2== 0) and(change_score == true)){
                    Score += 10
                    change_score = false
                }
                Log.i("TIME",current_time.toString())
                drawBitmap(background,0f,0f,null)

                //draw pause button
                drawBitmap(pause_button,860f,1700f,null)

                val message= "$Score pt"
                val textPaint = Paint().also {
                    it.color = Color.parseColor("#000000")
                    it.strokeWidth = 100f
                    it.strokeMiter = 100f
                    it.textSize=70f
                }
                canvas.drawText(message,0,message.length,50f,80f,textPaint)

                ///////draw the airplane with constraint on the x axe /////////
                if((roll<-0.701f)){
                    withTranslation(-495f, 0f) {
                        drawBitmap(airplane, 500f, 1400f, null) }
                    plane_x = 0f
                }else if ((roll>0.701f)or((roll *(500f/0.701f)+500f)>925f)){
                    withTranslation(425f, 0f) {
                        drawBitmap(airplane, 500f, 1400f, null) }
                    plane_x = 925f
                }else{
                    if((roll *(500f/0.701f)+500f)<925f){ /// to avoid airplane goes too much on the right, outside the screen
                        withTranslation(roll *(500f/0.701f), 0f) {
                            if (roll > oldRoll + 0.001) {
                                Log.i("OLDROLL", oldRoll.toString())
                                drawBitmap(airplaneRight, 500f, 1400f, null)
                            } else if (roll < oldRoll - 0.001) {
                                Log.i("OLDROLL", oldRoll.toString())
                                drawBitmap(airplaneLeft, 500f, 1400f, null)
                            } else {
                                drawBitmap(airplane, 500f, 1400f, null)
                            }
                        }
                        plane_x = 500f + roll *(500f/0.701f)
                    }
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
                    drawBitmap(bulletAvailable, 500f, 1720f, null)
                    drawBitmap(bulletAvailable, 550f, 1720f, null)
                    drawBitmap(bulletAvailable, 600f, 1720f, null)
                }else if(bullet_available==2){
                    drawBitmap(bulletAvailable, 500f, 1720f, null)
                    drawBitmap(bulletAvailable, 550f, 1720f, null)
                    // se vogliamo possiamo metter cuore vuoto
                }else if(bullet_available==1){
                    drawBitmap(bulletAvailable, 500f, 1720f, null)
                }
                //------ end draw bullet a disposizione -------//

                //Draw number of kills
                drawBitmap(skull, 20f, 1740f, null)
                val numKillsText = "$numKills"
                val killsPaint = Paint().also {
                    it.color = Color.parseColor("#000000")
                    it.strokeWidth = 100f
                    it.strokeMiter = 100f
                    it.textSize=70f
                }
                canvas.drawText(numKillsText,0, numKillsText.length,150f,1820f, killsPaint)

                //Spawn enemies
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
                        Log.i("boss","spawnto nemico 2")
                    }
                }
                if((!enemy_visible[3]) and (ex_array[3]) and (beat_boss1==true)){
                    GlobalScope.launch {
                        spawn_enemy(3)
                        Log.i("boss","spawnto nemico laterale")
                    }
                }
                //Log.i("boss",beat_boss1.toString())
                /*if(!enemy_visible[4] and ex_array[4]){
                    GlobalScope.launch {
                        spawn_enemy(4)
                    }
                }*/
                //////////////////////// spawn bosses //////////////////
                if((Score >500) and  (!boss1_spawned)){
                    GlobalScope.launch {
                        boss1_spawned = true
                        spawn_boss(0)
                    }
                }
                if((Score >2000) and (!boss2_spawned)){
                    GlobalScope.launch {
                        boss2_spawned = true
                        spawn_boss(1)
                    }
                }
                ///////////------------------ manage position of boss 1 -------------///////////////
                if((boss_visible[0])and(!beat_boss1)){
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
                /// increment velocity every 500 score and at most 2000 score
                if((Score==500) and (!augment_score[0])){
                    increment_velocity += 1
                    augment_score[0] = true
                }
                if((Score==1000) and (!augment_score[1])){
                    increment_velocity += 1
                    augment_score[1] = true
                }
                if((Score==1500) and (!augment_score[2])){
                    increment_velocity += 1
                    augment_score[2] = true
                }
                if((Score==2000) and (!augment_score[3])){
                    increment_velocity += 1
                    augment_score[3] = true
                }
                //////
                if(enemy_visible[0]) {
                    down1 += 7+(increment_velocity)
                    withTranslation (0f,down1) {
                        drawBitmap(array_enemy_type[array_type[0]], array_position[0], 0f, null)

                    }
                }
                if(enemy_visible[1]) {
                    down2 += 7+(increment_velocity)
                    withTranslation (0f,down2) {
                        drawBitmap(array_enemy_type[array_type[1]], array_position[1], 0f, null)

                    }
                }
                if(enemy_visible[2]) {
                    down3 += 7+(increment_velocity)
                    withTranslation (0f,down3) {
                        drawBitmap(array_enemy_type[array_type[2]], array_position[2], 0f, null)

                    }
                }
                if(enemy_visible[3]) {
                    down4 += 7+(increment_velocity)
                    if((lateral_movement[3]+array_position[3]>900f) and (dx_if_true[3] == true)){
                        dx_if_true[3] = false
                    }else if((lateral_movement[3]+array_position[3]<100f) and (dx_if_true[3]==false)){
                        dx_if_true[3] = true
                    }

                    if(dx_if_true[3]==true){lateral_movement[3] =lateral_movement[3]+12f}
                    if(dx_if_true[3]==false){lateral_movement[3] =lateral_movement[3]-12f}

                    withTranslation (lateral_movement[3],down4) {
                        drawBitmap(array_enemy_type[array_type[3]], array_position[3], 0f, null)

                    }
                }
                /*if(enemy_visible[4]) {
                    down5 += 7+(increment_velocity)
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
                }*/
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
                    lateral_movement[3] = 0f
                    justcollide[3] = false
                }
                /*if(down5 > 1720f){  //// reset down variable to respawn the enemies
                    down5 = 0f
                    enemy_visible[4] = false
                    ex_array[4] = true
                    lateral_movement[4] = 0f
                    dx_if_true[4] = true
                    justcollide[4] = false
                }*/
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
                if(is_visible_bul[0]) {
                    up[0] -= 40f
                    withTranslation(0f, up[0]) {
                        if (is_shot[0]) {
                            drawBitmap(bullet, plane_x+65f, 1400f, null)
                            res = plane_x+65f
                            is_shot[0] = false
                            bullet_position_x[0] = res
                            bullet_position_y[0] = (1400f+up[0])
                            if(bul == "normal") {
                                music.playShootSound(context)
                            } else{
                                music.playLaserSound(context)
                            }
                        } else {
                            drawBitmap(bullet, res, 1400f, null)
                            if((up[0])<-1400f){
                                is_visible_bul[0] = false
                                up[0] = 0f
                                if(bullet_available < 3) {
                                    bullet_available += 1
                                }
                                just_shot_bullet[0] = false
                            }
                            bullet_position_x[0] = (res)
                            bullet_position_y[0] = (1400f + up[0])
                        }
                    }
                }
                if(is_visible_bul[1]) {
                    up[1] -= 40f
                    withTranslation(0f, up[1]) {
                        if (is_shot[1]) {
                            drawBitmap(bullet, plane_x+65f, 1400f, null)
                            res2 =plane_x+65f
                            is_shot[1] = false
                            bullet_position_x[1] =res2
                            bullet_position_y[1] = (1400f+up[1])
                            if(bul == "normal") {
                                music.playShootSound(context)
                            } else{
                                music.playLaserSound(context)
                            }
                        } else {
                            drawBitmap(bullet, res2, 1400f, null)
                            if((up[1])<-1400f){
                                is_visible_bul[1] = false
                                up[1] = 0f
                                if(bullet_available < 3) {
                                    bullet_available += 1
                                }
                                just_shot_bullet[1] = false
                            }
                            bullet_position_x[1] = (res2)
                            bullet_position_y[1] = (1400f+up[1])
                        }
                    }
                }
                if(is_visible_bul[2]){
                    up[2] -= 40f
                    withTranslation(0f, up[2]) {
                        if (is_shot[2]) {
                            drawBitmap(bullet, plane_x+65f, 1400f, null)
                            res3 = plane_x+65f
                            is_shot[2] = false
                            bullet_position_x[2] = res3
                            bullet_position_y[2] = (1400f+up[2])
                            if(bul == "normal") {
                                music.playShootSound(context)
                            } else{
                                music.playLaserSound(context)
                            }
                        } else {
                            drawBitmap(bullet, res3, 1400f, null)
                            Log.i("SCONTRO", bullet_position_y[2].toString()+", "+(enemy_position_y[0] + 150f).toString())
                            if((up[2])<-1400f){
                                //Log.i("ROLL","sta usando il terzo bullet")
                                is_visible_bul[2] = false
                                up[2] = 0f
                                if(bullet_available < 3) {
                                    bullet_available += 1
                                }
                                just_shot_bullet[2] = false
                            }
                            bullet_position_x[2] = (res3)
                            bullet_position_y[2] = (1400f+up[2])
                        }
                    }
                }
                else{
                    Log.i("BUL1", is_visible_bul[2].toString())
                }
                ///////// boss1 bullet /////////////
                if(boss_visible[0]){
                    upboss[0]+= 20f
                    withTranslation(0f, upboss[0]) {
                        if (shotboss[0]) {
                            drawBitmap(bullet, lateral_movement_boss[0]+boss_x[0]+90f, (boss_y[0]+200f), null)
                            resboss = lateral_movement_boss[0]+boss_x[0]+90f
                            resbossy = (boss_y[0]+200f)
                            shotboss[0] = false
                            boss_bullet_position_x[0] = resboss
                            boss_bullet_position_y[0] = (resbossy)
                        } else {
                            drawBitmap(bullet, resboss, (resbossy), null)
                            if((upboss[0])>1400f){
                                upboss[0] = 0f
                                shotboss[0]= true
                                just_shot_bullet_boss[0] = false
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
                            drawBitmap(bullet, lateral_movement_boss[1]+boss_x[1]+90f, (boss_y[1]+200f), null)
                            resboss2_1 = lateral_movement_boss[1]+boss_x[1]+90f
                            resbossy2_1 = (upboss[1]+boss_y[1]+200f)
                            shotboss[1] = false
                            boss_bullet_position_x[1] = resboss2_1
                            boss_bullet_position_y[1] = (resbossy2_1)
                        } else {
                            drawBitmap(bullet, resboss2_1, (resbossy2_1), null)
                            if((upboss[1])>1400f){
                                upboss[1] = 0f
                                shotboss[1]= true
                                just_shot_bullet_boss[1]= false
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
                            drawBitmap(bullet, lateral_movement_boss[1]+boss_x[1]+90f,(boss_y[1]+200f), null)
                            resboss2_2 = lateral_movement_boss[1]+boss_x[1]+90f
                            resbossy2_2 = (upboss[1]+boss_y[1]+200f)
                            shotboss[1] = false
                            boss_bullet_position_x[1] = resboss2_2
                            boss_bullet_position_y[1] = (resbossy2_2)
                        }else {
                            drawBitmap(bullet, resboss2_2, (resbossy2_2), null)
                            if((upboss[1])>1400f){
                                upboss[1] = 0f
                                shotboss[1]= true
                                just_shot_bullet_boss[2] = false
                            }
                            boss_bullet_position_x[1] = (resboss2_2)
                            boss_bullet_position_y[1] = (upboss[1]+resbossy2_2)
                        }
                    }
                }


                //////////////////  ------------- MANAGE COLLISION OUR PLANE WITH ENEMIES   ----------------- ////////////////
                if( (array_position[0]>= plane_x-140f)and(array_position[0]<=plane_x+140f)and(enemy_position_y[0] <= plane_y+300f)and(enemy_position_y[0] >= plane_y-150f)){
                    if(!justcollide[0]) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            Log.i("COLPITO", "1")
                            music.playCollisionSound(context)
                            hearts -= 1
                            justcollide[0] = true
                        }
                    }
                }
                if( (array_position[1]>= plane_x-140f)and(array_position[1]<=plane_x+140f)and(enemy_position_y[1] <= plane_y+300f)and(enemy_position_y[1] >= plane_y-150f)){
                    if(!justcollide[1]) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            Log.i("COLPITO", "2")
                            music.playCollisionSound(context)
                            hearts -= 1
                            justcollide[1] = true
                        }
                    }
                }
                if( (array_position[2]>= plane_x-140f)and(array_position[2]<=plane_x+140f)and(enemy_position_y[2] <= plane_y+300f)and(enemy_position_y[2] >= plane_y-150f)){
                    Log.i("COLPITO", "3")
                    if(!justcollide[2]) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            music.playCollisionSound(context)
                            hearts -= 1
                            justcollide[2] = true
                        }
                    }
                }
                if( (array_position[3]+lateral_movement[3]>= plane_x-140f)and(array_position[3]+lateral_movement[3]<=plane_x+140f)and(enemy_position_y[3] <= plane_y+300f)and(enemy_position_y[3] >= plane_y-150f)){
                    Log.i("COLPITO", "4")
                    if(!justcollide[3]) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            music.playCollisionSound(context)
                            hearts -= 1
                            justcollide[3] = true
                        }
                    }
                }
                if( (lateral_movement[4]+array_position[4]>= plane_x-15f)and(lateral_movement[4]+array_position[4]<=plane_x+15f)and(enemy_position_y[4] == plane_y)){
                    Log.i("COLPITO", "SI")
                    if(!justcollide[4]) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            music.playCollisionSound(context)
                            hearts -= 1
                            justcollide[4] = true
                        }
                    }
                }
                ////////////////  ------------------------------------------- ////////////////////

                ////////////////  ------------- BOSS BULLET MANAGEMENT  ----------------- ////////////////
                ////////////boss 1 bullet
                if((boss_bullet_position_x[0]>= plane_x-15f) and (boss_bullet_position_x[0]<=plane_x+165f)
                    and(boss_bullet_position_y[0] <= plane_y+190f)and(boss_bullet_position_y[0] >= plane_y-15f)){
                    if(just_shot_bullet_boss[0]==false) {////// to avoid multiple collision of same bullet
                        if (hearts == 1) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot_bullet_boss[0]=true
                        }
                    }
                }
                ///////boss 2 bullets
                if((boss_bullet_position_x[1]>= plane_x-15f) and (boss_bullet_position_x[1]<=plane_x+165f)
                    and(boss_bullet_position_y[1] <= plane_y+190f)and(boss_bullet_position_y[1] >= plane_y-15f)){
                    if(just_shot_bullet_boss[1]==false) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot_bullet_boss[1]=true
                        }
                    }
                }
                if((boss_bullet_position_x[2] >=plane_x -15f) and (boss_bullet_position_x[2] <=plane_x +165f)and
                    (boss_bullet_position_y[2] <=plane_y +190f) and(boss_bullet_position_y[2] >=plane_y -15f)){
                    if(just_shot_bullet_boss[2]==false) {
                        if (hearts == 1) {
                            start = false
                        } else {
                            hearts -= 1
                            just_shot_bullet_boss[2]=true
                        }
                    }
                }
                ///////////////////////// ---------------------------------- ////////////////////////////////

                /////////////////////////////// -------------- MANAGE COLLISION OF OUR PLANE BULLETS TO ENEMIES  ----------------- ///////////////////////////
                ///////////////bullet 1
                if(is_visible_bul[0]) {
                    if ((bullet_position_x[0] >= array_position[0] - 35f) and (bullet_position_x[0] <= array_position[0] + 145f) and (bullet_position_y[0] <= enemy_position_y[0] + 150f) and (bullet_position_y[0] >= enemy_position_y[0])) {
                        if ((!just_shot_bullet[0]) and (enemy_visible[0])) {
                            Score += 50
                            numKills += 1
                            down1 = 0f
                            enemy_visible[0] = false
                            is_visible_bul[0] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[0] = 0f                      //Put the y cordinate of bullet at the starting position
                            ex_array[0] = true
                            isExpBul1[0] = true
                            expPos1[0][0] = array_position[0]
                            expPos1[0][1] = enemy_position_y[0]
                            //drawBitmap(explosion,array_position[0],enemy_position_y[0],null)/// gif for the explosion
                            just_shot_bullet[0] = true
                        }
                    }
                    if ((bullet_position_x[0] >= array_position[1] - 35f) and (bullet_position_x[0] <= array_position[1] + 145f) and (bullet_position_y[0] <= enemy_position_y[1] + 150f) and (bullet_position_y[0] >= enemy_position_y[1])) {
                        if ((!just_shot_bullet[0]) and (enemy_visible[1])) {
                            Score += 50
                            numKills += 1
                            down2 = 0f
                            enemy_visible[1] = false
                            is_visible_bul[0] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[0] =
                                0f                      //Put the bullet at the starting position
                            ex_array[1] = true
                            isExpBul1[1] = true
                            expPos1[1][0] = array_position[1]
                            expPos1[1][1] = enemy_position_y[1]
                            //drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                            just_shot_bullet[0] = true
                        }
                    }
                    if ((bullet_position_x[0] >= array_position[2] - 35f) and (bullet_position_x[0] <= array_position[2] + 145f) and (bullet_position_y[0] <= enemy_position_y[2] + 150f) and (bullet_position_y[0] >= enemy_position_y[2])) {
                        if ((!just_shot_bullet[0]) and (enemy_visible[2])) {
                            Score += 50
                            numKills += 1
                            down3 = 0f
                            enemy_visible[2] = false
                            is_visible_bul[0] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[0] =
                                0f                      //Put the bullet at the starting position
                            ex_array[2] = true
                            isExpBul1[2] = true
                            expPos1[2][0] = array_position[2]
                            expPos1[2][1] = enemy_position_y[2]
                            //drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                            just_shot_bullet[0] = true
                        }
                    }
                    if ((bullet_position_x[0] >= array_position[3]+lateral_movement[3]  - 35f) and (bullet_position_x[0] <= array_position[3]+lateral_movement[3]  + 145f) and (bullet_position_y[0] <= enemy_position_y[3] + 150f) and (bullet_position_y[0] >= enemy_position_y[3])) {
                        if ((!just_shot_bullet[0]) and (enemy_visible[3])) {
                            Score += 50
                            numKills += 1
                            down4 = 0f
                            lateral_movement[3]= 0f
                            enemy_visible[3] = false
                            is_visible_bul[0] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[0] =
                                0f                      //Put the bullet at the starting position
                            ex_array[3] = true
                            isExpBul1[3] = true
                            expPos1[3][0] = array_position[3]+lateral_movement[3]
                            expPos1[3][1] = enemy_position_y[3]
                            //drawBitmap(explosion,array_position[3],enemy_position_y[3],null)
                            just_shot_bullet[0] = true
                        }
                    }
                    if ((bullet_position_x[0] >= lateral_movement[4] + array_position[4] - 35f) and (bullet_position_x[0] <= lateral_movement[4] + array_position[4] + 115f) and (bullet_position_y[0] <= enemy_position_y[4] + 120f) and (bullet_position_y[0] >= enemy_position_y[4])) {
                        if ((!just_shot_bullet[0]) and (enemy_visible[4])) {
                            Score += 50
                            numKills += 1
                            down5 = 0f
                            enemy_visible[4] = false
                            is_visible_bul[0] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[0] =
                                0f                      //Put the bullet at the starting position
                            ex_array[4] = true
                            isExpBul1[4] = true
                            expPos1[4][0] = array_position[4]
                            expPos1[4][1] = enemy_position_y[4]
                            just_shot_bullet[0] = true
                        }
                    }
                }

                /////////////////ESPLOSIONE BULLET 1
                if (isExpBul1[0]) {
                    if (expFrame1[0] < 18) {
                        if(expFrame1[0] == 0){
                            just_shot_bullet[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame1[0] / 2),
                            expPos1[0][0],
                            expPos1[0][1],
                            null
                        )
                        expFrame1[0] = expFrame1[0] + 1
                    } else {
                        expFrame1[0] = 0
                        isExpBul1[0] = false
                    }
                }
                if (isExpBul1[1]) {
                    if (expFrame1[1] < 18) {
                        if(expFrame1[1] == 0){
                            just_shot_bullet[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame1[1] / 2),
                            expPos1[1][0],
                            expPos1[1][1],
                            null
                        )
                        expFrame1[1] = expFrame1[1] + 1
                    } else {
                        expFrame1[1] = 0
                        isExpBul1[1] = false
                    }
                }
                if (isExpBul1[2]) {
                    if (expFrame1[2] < 18) {
                        if(expFrame1[2] == 0){
                            just_shot_bullet[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame1[2] / 2),
                            expPos1[2][0],
                            expPos1[2][1],
                            null
                        )
                        expFrame1[2] = expFrame1[2] + 1
                    } else {
                        expFrame1[2] = 0
                        isExpBul1[2] = false
                    }
                }
                if (isExpBul1[3]) {
                    if (expFrame1[3] < 18) {
                        if(expFrame1[3] == 0){
                            just_shot_bullet[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame1[3] / 2),
                            expPos1[3][0],
                            expPos1[3][1],
                            null
                        )
                        expFrame1[3] = expFrame1[3] + 1
                    } else {
                        expFrame1[3] = 0
                        isExpBul1[3] = false
                    }
                }
                if (isExpBul1[4]) {
                    if (expFrame1[4] < 18) {
                        if(expFrame1[4] == 0){
                            just_shot_bullet[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame1[4] / 2),
                            expPos1[4][0],
                            expPos1[4][1],
                            null
                        )
                        expFrame1[4] = expFrame1[4] + 1
                    } else {
                        expFrame1[4] = 0
                        isExpBul1[4] = false
                    }
                }
                /////////////

                //////////////bullet 2 //////////////////////////
                if(is_visible_bul[1]) {
                    if ((bullet_position_x[1] >= array_position[0] - 35f) and (bullet_position_x[1] <= array_position[0] + 145f) and (bullet_position_y[1] <= enemy_position_y[0] + 150f) and (bullet_position_y[1] >= enemy_position_y[0])) {
                        if ((!just_shot_bullet[1]) and (enemy_visible[0])) {
                            Score += 50
                            numKills += 1
                            down1 = 0f
                            enemy_visible[0] = false
                            is_visible_bul[1] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[1] = 0f                      //Put the bullet at the starting position
                            ex_array[0] = true
                            isExpBul2[0] = true
                            expPos2[0][0] = array_position[0]
                            expPos2[0][1] = enemy_position_y[0]
                            //drawBitmap(explosion,array_position[0],enemy_position_y[0],null)
                            just_shot_bullet[1] = true
                        }
                    }
                    if ((bullet_position_x[1] >= array_position[1] - 35f) and (bullet_position_x[1] <= array_position[1] + 145f) and (bullet_position_y[1] <= enemy_position_y[1] + 150f) and (bullet_position_y[1] >= enemy_position_y[1])) {
                        if ((!just_shot_bullet[1]) and (enemy_visible[1])) {
                            Score += 50
                            numKills += 1
                            down2 = 0f
                            enemy_visible[1] = false
                            is_visible_bul[1] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[1] = 0f                      //Put the bullet at the starting position
                            ex_array[1] = true
                            isExpBul2[1] = true
                            expPos2[1][0] = array_position[1]
                            expPos2[1][1] = enemy_position_y[1]
                            //drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                            just_shot_bullet[1] = true
                        }
                    }
                    if ((bullet_position_x[1] >= array_position[2] - 35f) and (bullet_position_x[1] <= array_position[2] + 145f) and (bullet_position_y[1] <= enemy_position_y[2] + 150f) and (bullet_position_y[1] >= enemy_position_y[2])) {
                        if ((!just_shot_bullet[1]) and (enemy_visible[2])) {
                            Score += 50
                            numKills += 1
                            down3 = 0f
                            enemy_visible[2] = false
                            is_visible_bul[1] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[1] =
                                0f                      //Put the bullet at the starting position
                            ex_array[2] = true
                            isExpBul2[2] = true
                            expPos2[2][0] = array_position[2]
                            expPos2[2][1] = enemy_position_y[2]
                            //drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                            just_shot_bullet[1] = true
                        }
                    }
                    if ((bullet_position_x[1] >= array_position[3]+lateral_movement[3]  - 35f) and (bullet_position_x[1] <= array_position[3] +lateral_movement[3] + 145f) and (bullet_position_y[1] <= enemy_position_y[3] + 150f) and (bullet_position_y[1] >= enemy_position_y[3])) {
                        if ((!just_shot_bullet[1]) and (enemy_visible[3])) {
                            Score += 50
                            numKills += 1
                            down4 = 0f
                            lateral_movement[3]= 0f
                            enemy_visible[3] = false
                            is_visible_bul[1] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[1] =
                                0f                      //Put the bullet at the starting position
                            ex_array[3] = true
                            isExpBul2[3] = true
                            expPos2[3][0] = array_position[3]+lateral_movement[3]
                            expPos2[3][1] = enemy_position_y[3]
                            //drawBitmap(explosion,array_position[3],enemy_position_y[3],null)
                            just_shot_bullet[1] = true
                        }
                    }
                    if ((bullet_position_x[1] >= lateral_movement[4] + array_position[4] - 35f) and (bullet_position_x[1] <= lateral_movement[4] + array_position[4] + 115f) and (bullet_position_y[1] <= enemy_position_y[4] + 120f) and (bullet_position_y[1] >= enemy_position_y[4])) {
                        if ((!just_shot_bullet[1]) and (enemy_visible[4])) {
                            Score += 50
                            numKills += 1
                            down5 = 0f
                            enemy_visible[4] = false
                            is_visible_bul[1] = false
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[1] =
                                0f                      //Put the bullet at the starting position
                            ex_array[4] = true
                            isExpBul2[4] = true
                            expPos2[4][0] = array_position[4]
                            expPos2[4][1] = enemy_position_y[4]
                            //drawBitmap(explosion,array_position[4],enemy_position_y[4],null)
                            just_shot_bullet[1] = true
                        }
                    }
                }

                /////////////////ESPLOSIONE BULLET 2
                if (isExpBul2[0]) {
                    if (expFrame2[0] < 18) {
                        if(expFrame2[0] == 0){
                            just_shot_bullet[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame2[0] / 2),
                            expPos2[0][0],
                            expPos2[0][1],
                            null
                        )
                        expFrame2[0] = expFrame2[0] + 1
                    } else {
                        expFrame2[0] = 0
                        isExpBul2[0] = false
                    }
                }
                if (isExpBul2[1]) {
                    if (expFrame2[1] < 18) {
                        if(expFrame2[1] == 0){
                            just_shot_bullet[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame2[1] / 2),
                            expPos2[1][0],
                            expPos2[1][1],
                            null
                        )
                        expFrame2[1] = expFrame2[1] + 1
                    } else {
                        expFrame2[1] = 0
                        isExpBul2[1] = false
                    }
                }
                if (isExpBul2[2]) {
                    if (expFrame2[2] < 18) {
                        if(expFrame2[2] == 0){
                            just_shot_bullet[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame2[2] / 2),
                            expPos2[2][0],
                            expPos2[2][1],
                            null
                        )
                        expFrame2[2] = expFrame2[2] + 1
                    } else {
                        expFrame2[2] = 0
                        isExpBul2[2] = false
                    }
                }
                if (isExpBul2[3]) {
                    if (expFrame2[3] < 18) {
                        if(expFrame2[3] == 0){
                            just_shot_bullet[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame2[3] / 2),
                            expPos2[3][0],
                            expPos2[3][1],
                            null
                        )
                        expFrame2[3] = expFrame2[3] + 1
                    } else {
                        expFrame2[3] = 0
                        isExpBul2[3] = false
                    }
                }
                if (isExpBul2[4]) {
                    if (expFrame2[4] < 18) {
                        if(expFrame2[4] == 0){
                            just_shot_bullet[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame2[4] / 2),
                            expPos2[4][0],
                            expPos2[4][1],
                            null
                        )
                        expFrame1[4] = expFrame1[4] + 1
                    } else {
                        expFrame2[4] = 0
                        isExpBul2[4] = false
                    }
                }
                ////////////////

                //////////////////////bullet 3 ////////////////////////////
                if(is_visible_bul[2]) {
                    if ((bullet_position_x[2] >= array_position[0] - 35f) and (bullet_position_x[2] <= array_position[0] + 145f) and (bullet_position_y[2] <= enemy_position_y[0] + 150f) and (bullet_position_y[2] >= enemy_position_y[0])) {
                        if ((!just_shot_bullet[2]) and (enemy_visible[0])) {
                            Log.i("COLLISIONE", "AEREO 1")
                            Score += 50
                            numKills += 1
                            down1 = 0f
                            enemy_visible[0] = false
                            is_visible_bul[2] = false
                            Log.i("prova",enemy_visible[0].toString())
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[2] = 0f                      //Put the bullet at the starting position
                            ex_array[0] = true
                            isExpBul3[0] = true
                            expPos3[0][0] = array_position[0]
                            expPos3[0][1] = enemy_position_y[0]
                            //drawBitmap(explosion,array_position[0],enemy_position_y[0],null)
                            just_shot_bullet[2] = true
                        }
                    }
                    if ((bullet_position_x[2] >= array_position[1] - 35f) and (bullet_position_x[2] <= array_position[1] + 145f) and (bullet_position_y[2] <= enemy_position_y[1] + 150f) and (bullet_position_y[2] >= enemy_position_y[1])) {
                        if ((!just_shot_bullet[2]) and (enemy_visible[1])) {
                            Log.i("COLLISIONE", "AEREO 2")
                            Score += 50
                            numKills += 1
                            down2 = 0f
                            enemy_visible[1] = false
                            is_visible_bul[2] = false
                            Log.i("prova",enemy_visible[1].toString())
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[2] =
                                0f                      //Put the bullet at the starting position
                            ex_array[1] = true
                            isExpBul3[1] = true
                            expPos3[1][0] = array_position[1]
                            expPos3[1][1] = enemy_position_y[1]
                            //drawBitmap(explosion,array_position[1],enemy_position_y[1],null)
                            just_shot_bullet[2] = true
                        }
                    }
                    if ((bullet_position_x[2] >= array_position[2] - 35f) and (bullet_position_x[2] <= array_position[2] + 145f) and (bullet_position_y[2] <= enemy_position_y[2] + 150f) and (bullet_position_y[2] >= enemy_position_y[2])) {
                        if ((!just_shot_bullet[2]) and (enemy_visible[2])) {
                            Log.i("COLLISIONE", "AEREO 3")
                            Score += 50
                            numKills += 1
                            down3 = 0f
                            enemy_visible[2] = false
                            is_visible_bul[2] = false
                            Log.i("prova",enemy_visible[2].toString())
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[2] =
                                0f                      //Put the bullet at the starting position
                            ex_array[2] = true
                            isExpBul3[2] = true
                            expPos3[2][0] = array_position[2]
                            expPos3[2][1] = enemy_position_y[2]
                            //drawBitmap(explosion,array_position[2],enemy_position_y[2],null)
                            just_shot_bullet[2] = true
                        }
                    }
                    if ((bullet_position_x[2] >= array_position[3]+lateral_movement[3]  - 35f) and (bullet_position_x[2] <= array_position[3]+lateral_movement[3]  + 145f) and (bullet_position_y[2] <= enemy_position_y[3] + 150f) and (bullet_position_y[2] >= enemy_position_y[3])) {
                        if ((!just_shot_bullet[2]) and (enemy_visible[3])) {
                            Log.i("COLLISIONE", "AEREO 4")
                            Score += 50
                            numKills += 1
                            down4 = 0f
                            lateral_movement[3]= 0f
                            enemy_visible[3] = false
                            is_visible_bul[2] = false
                            Log.i("prova",enemy_visible[3].toString())
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[2] =
                                0f                      //Put the bullet at the starting position
                            ex_array[3] = true
                            isExpBul3[3] = true
                            expPos3[3][0] = array_position[3]+ lateral_movement[3]
                            expPos3[3][1] = enemy_position_y[3]
                            just_shot_bullet[2] = true
                        }
                    }
                    if ((bullet_position_x[2] >= lateral_movement[4] + array_position[4] - 35f) and (bullet_position_x[2] <= lateral_movement[4] + array_position[4] + 115f) and (bullet_position_y[2] <= enemy_position_y[4] + 120f) and (bullet_position_y[2] >= enemy_position_y[4])) {
                        if ((!just_shot_bullet[2]) and (enemy_visible[4])) {
                            Log.i("COLLISIONE", "AEREO 5")
                            Score += 50
                            numKills += 1
                            down5 = 0f
                            enemy_visible[4] = false
                            is_visible_bul[2] = false
                            Log.i("prova",enemy_visible[4].toString())
                            if (bullet_available < 3) {
                                bullet_available += 1       //Bullet retruns available
                            }
                            up[2] =
                                0f                      //Put the bullet at the starting position
                            ex_array[4] = true
                            isExpBul3[4] = true
                            expPos3[4][0] = array_position[4]
                            expPos3[4][1] = enemy_position_y[4]
                            just_shot_bullet[2] = true
                        }
                    }
                }

                /////////////////ESPLOSIONE BULLET 3
                if (isExpBul3[0]) {
                    if (expFrame3[0] < 18) {
                        if(expFrame3[0] == 0){
                            just_shot_bullet[2] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame3[0] / 2),
                            expPos3[0][0],
                            expPos3[0][1],
                            null
                        )
                        expFrame3[0] = expFrame3[0] + 1
                    } else {
                        expFrame3[0] = 0
                        isExpBul3[0] = false
                    }
                }
                if (isExpBul3[1]) {
                    if (expFrame3[1] < 18) {
                        if(expFrame3[1] == 0){
                            just_shot_bullet[2] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame3[1] / 2),
                            expPos3[1][0],
                            expPos3[1][1],
                            null
                        )
                        expFrame3[1] = expFrame3[1] + 1
                    } else {
                        expFrame3[1] = 0
                        isExpBul3[1] = false
                    }
                }
                if (isExpBul3[2]) {
                    if (expFrame3[2] < 18) {
                        if(expFrame3[2] == 0){
                            just_shot_bullet[2] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame3[2] / 2),
                            expPos3[2][0],
                            expPos3[2][1],
                            null
                        )
                        expFrame3[2] = expFrame3[2] + 1
                    } else {
                        expFrame3[2] = 0
                        isExpBul3[2] = false
                    }
                }
                if (isExpBul3[3]) {
                    if (expFrame3[3] < 18) {
                        if(expFrame3[3] == 0){
                            just_shot_bullet[2] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame3[3] / 2),
                            expPos3[3][0],
                            expPos3[3][1],
                            null
                        )
                        expFrame3[3] = expFrame3[3] + 1
                    } else {
                        expFrame3[3] = 0
                        isExpBul3[3] = false
                    }
                }
                if (isExpBul3[4]) {
                    if (expFrame3[4] < 18) {
                        if(expFrame3[4] == 0){
                            just_shot_bullet[2] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideo.get(expFrame3[4] / 2),
                            expPos3[4][0],
                            expPos3[4][1],
                            null
                        )
                        expFrame3[4] = expFrame3[4] + 1
                    } else {
                        expFrame3[4] = 0
                        isExpBul3[4] = false
                    }
                }

                //////////////////////***************************************************//////////////////////////////////////////

                ///////////////******** MANAGE COLLISION OUR BULLETS TO BOSS 1 *******////////////////
                if((bullet_position_x[0]>=boss_x[0]+lateral_movement_boss[0]-15f)and(bullet_position_x[0]<=boss_x[0]+lateral_movement_boss[0]+215f)and(bullet_position_y[0]<=boss_y[0]+215f)and(bullet_position_y[0]>=boss_y[0]-15f)) {
                    if(!just_shot_bullet[0]){
                        just_shot_bullet[0] = true
                        if(life_boss[0]>0) {
                            ////// check if boss have still some lives, otherwise set the boss invisible(dead)
                            if (life_boss[0] == 1) {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                                boss_visible[0] = false
                                Score += 200
                                beat_boss1 = true
                                numKills += 1
                                isExpBul1_Boss[0] = true
                                expPos1_Boss[0][0] = boss_x[0]+lateral_movement_boss[0]
                                expPos1_Boss[0][1] = boss_y[0]
                            } else {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                            }
                        }
                    }
                }
                if((bullet_position_x[1]>=boss_x[0]+lateral_movement_boss[0]-15f)and(bullet_position_x[1]<=boss_x[0]+lateral_movement_boss[0]+215f)and(bullet_position_y[1]<=boss_y[0]+215f)and(bullet_position_y[1]>=boss_y[0]-15f)) {
                    if(!just_shot_bullet[1]){
                        just_shot_bullet[1] = true
                        if(life_boss[0]>0) {
                            if (life_boss[0] == 1) {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                                boss_visible[0] = false
                                Score += 200
                                beat_boss1 = true
                                numKills += 1
                                isExpBul1_Boss[0] = true
                                expPos1_Boss[0][0] = boss_x[0]+lateral_movement_boss[0]
                                expPos1_Boss[0][1] = boss_y[0]
                            } else {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                            }
                        }
                    }
                }
                if((bullet_position_x[2]>=boss_x[0]+lateral_movement_boss[0]-15f)and(bullet_position_x[2]<=boss_x[0]+lateral_movement_boss[0]+215f)and(bullet_position_y[2]<=boss_y[0]+215f)and(bullet_position_y[2]>=boss_y[0]-15f)) {
                    if(!just_shot_bullet[2]){
                        just_shot_bullet[2] = true
                        if(life_boss[0]>0) {
                            if (life_boss[0]== 1) {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                                boss_visible[0] = false
                                Score += 200
                                beat_boss1 = true
                                numKills += 1
                                isExpBul1_Boss[0] = true
                                expPos1_Boss[0][0] = boss_x[0]+lateral_movement_boss[0]
                                expPos1_Boss[0][1] = boss_y[0]
                            } else {
                                Log.i("prova", life_boss[0].toString())
                                life_boss[0] -= 1
                            }
                        }
                    }
                }
                //////////******** OUR BULLET HIT BOSS 2 *******////////////////
                if((bullet_position_x[0]>=boss_x[1]+lateral_movement_boss[1]-15f)and(bullet_position_x[0]<=boss_x[1]+lateral_movement_boss[1]+215f)and(bullet_position_y[0]<=boss_y[1]+215f)and(bullet_position_y[0]>=boss_y[1]-15f)) {
                    if(!just_shot_bullet[0]){
                        just_shot_bullet[1] = true
                        if(life_boss[1]==1){
                            life_boss[1] -=1
                            boss_visible[1]= false
                            Score += 200
                            numKills += 1
                            isExpBul1_Boss[1] = true
                            expPos1_Boss[1][0] = boss_x[1]+lateral_movement_boss[1]
                            expPos1_Boss[1][1] = boss_y[1]
                        }else{
                            life_boss[1] -= 1
                        }
                    }
                }
                if((bullet_position_x[1]>=boss_x[1]-15f)and(bullet_position_x[1]<=boss_x[1]+215f)and(bullet_position_y[1]<=boss_y[1]+215f)and(bullet_position_y[1]>=boss_y[1]-15f)) {
                    if(!just_shot_bullet[1]){
                        just_shot_bullet[1] = true
                        if(life_boss[1]==1){
                            life_boss[1] -=1
                            boss_visible[1]= false
                            Score += 200
                            numKills += 1
                            isExpBul1_Boss[1] = true
                            expPos1_Boss[1][0] = boss_x[1]
                            expPos1_Boss[1][1] = boss_y[1]
                        }else{
                            life_boss[1] -= 1
                        }
                    }
                }
                if((bullet_position_x[2]>=boss_x[1]-15f)and(bullet_position_x[2]<=boss_x[1]+215f)and(bullet_position_y[2]<=boss_y[1]+215f)and(bullet_position_y[2]>=boss_y[1]-15f)) {
                    if(!just_shot_bullet[2]){
                        just_shot_bullet[2] = true
                        if(life_boss[1]==1){
                            life_boss[1] -=1
                            boss_visible[1]= false
                            Score += 200
                            numKills += 1
                            isExpBul1_Boss[1] = true
                            expPos1_Boss[1][0] = boss_x[1]
                            expPos1_Boss[1][1] = boss_y[1]
                        }else{
                            life_boss[1] -= 1
                        }
                    }
                }
                //////////////// ********** END COLLISION BULLET WITH BOSS *******************///////////////

                //////////////////////// EXPLOSION BOSS  /////////////////////////////////////////
                /////////////////ESPLOSIONE BULLET 1 BOSS 1
                if (isExpBul1_Boss[0]) {
                    if (expFrame1_Boss[0] < 18) {
                        if(expFrame1_Boss[0] == 0){
                            just_shot_bullet_boss[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame1_Boss[0] / 2),
                            expPos1_Boss[0][0],
                            expPos1_Boss[0][1],
                            null
                        )
                        expFrame1_Boss[0] = expFrame1_Boss[0] + 1
                    } else {
                        expFrame1_Boss[0] = 0
                        isExpBul1_Boss[0] = false
                    }
                }
                /////////////////ESPLOSIONE BULLET 1 BOSS 2
                if (isExpBul1_Boss[1]) {
                    if (expFrame1_Boss[1] < 18) {
                        if(expFrame1_Boss[1] == 0){
                            just_shot_bullet_boss[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame1_Boss[1] / 2),
                            expPos1_Boss[1][0],
                            expPos1_Boss[1][1],
                            null
                        )
                        expFrame1_Boss[1] = expFrame1_Boss[1] + 1
                    } else {
                        expFrame1_Boss[1] = 0
                        isExpBul1_Boss[1] = false
                    }
                }
                /////////////////ESPLOSIONE BULLET 2 BOSS 1
                if (isExpBul2_Boss[0]) {
                    if (expFrame2_Boss[0] < 18) {
                        if(expFrame2_Boss[0] == 0){
                            just_shot_bullet_boss[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame1_Boss[0] / 2),
                            expPos2_Boss[0][0],
                            expPos2_Boss[0][1],
                            null
                        )
                        expFrame2_Boss[0] = expFrame2_Boss[0] + 1
                    } else {
                        expFrame2_Boss[0] = 0
                        isExpBul2_Boss[0] = false
                    }
                }
                /////////////////ESPLOSIONE BULLET 2 BOSS 2
                if (isExpBul2_Boss[1]) {
                    if (expFrame2_Boss[1] < 18) {
                        if(expFrame2_Boss[1] == 0){
                            just_shot_bullet_boss[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame2_Boss[1] / 2),
                            expPos2_Boss[1][0],
                            expPos2_Boss[1][1],
                            null
                        )
                        expFrame2_Boss[1] = expFrame2_Boss[1] + 1
                    } else {
                        expFrame2_Boss[1] = 0
                        isExpBul2_Boss[1] = false
                    }
                }
                /////////////////ESPLOSIONE BULLET 3 BOSS 1
                if (isExpBul3_Boss[0]) {
                    if (expFrame3_Boss[0] < 18) {
                        if(expFrame3_Boss[0] == 0){
                            just_shot_bullet_boss[0] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame3_Boss[0] / 2),
                            expPos3_Boss[0][0],
                            expPos3_Boss[0][1],
                            null
                        )
                        expFrame3_Boss[0] = expFrame3_Boss[0] + 1
                    } else {
                        expFrame3_Boss[0] = 0
                        isExpBul3_Boss[0] = false
                    }
                }
                /////////////////ESPLOSIONE BULLET 3 BOSS 2
                if (isExpBul3_Boss[1]) {
                    if (expFrame3_Boss[1] < 18) {
                        if(expFrame3_Boss[1] == 0){
                            just_shot_bullet_boss[1] = false
                            //If there is an explosion play its sound
                            music.playExplosionSound(context)
                        }
                        drawBitmap(
                            bitmapVideoBoss.get(expFrame3_Boss[1] / 2),
                            expPos3_Boss[1][0],
                            expPos3_Boss[1][1],
                            null
                        )
                        expFrame3_Boss[1] = expFrame3_Boss[1] + 1
                    } else {
                        expFrame3_Boss[1] = 0
                        isExpBul3_Boss[1] = false
                    }
                }

                ///////////////////////////***************************************************/////////////////////////////////////

                //////////////// PAUSE AND GAMEOVER MANAGEMENT //////////////////////////////
            }else if(PAUSE and !start){
                val message="GAME PAUSED"
                val textPaint = Paint().also {
                    it.color = Color.parseColor("#000000")
                    it.strokeWidth = 100f
                    it.textSize=130f
                }
                drawBitmap(background,0f,0f,null)
                canvas.drawBitmap(pause_label,130f,300f,null)
                canvas.drawBitmap(resume_button,130f,1000f,null)
                canvas.drawBitmap(quit_button,130f,1300f,null)
                drawText(message,100f,300f,textPaint)
            }else if(!PAUSE and !start) {
                ///////// gameover ////////////
                gameover = true
                if((Score > old_score) and (logged)){
                    //Connecting to Firebase Database
                    val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

                    val referenceOldScore = database.getReference("Users/$current_id/Score")
                    referenceOldScore.setValue(Score.toString())
                    old_score = Score
                    current_score = Score
                }
                plane_x = 500f
                hearts = 3
                val intent = Intent(context, GameoverActivity::class.java)
                //intent.putExtra("Opponent", opponent)
                intent.putExtra("Color", col)
                intent.putExtra("Bullet", bul)
                intent.putExtra("Score", Score.toString())
                music.stopSound()
                startActivity(context, intent, null)

                Score = 0
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

        //Save the old roll angle
        oldRoll = roll

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
                if((!PAUSE) and (((event.getX()>860f) and (event.getX()<1060)) and ((event.getY()>1700f) and (event.getY()<1900)))){
                    PAUSE = true
                    music.pauseSound()
                    start = false
                }else if(PAUSE and ((event.getX()>130f)and (event.getX()<930f)and (event.getY()>1000f)and (event.getY()<1200f))){
                    start = true
                    music.playSoundGame(context)
                    PAUSE = false
                    return_to_game = true
                }else if(PAUSE and((event.getX()>130f)and (event.getX()<930f)and (event.getY()>1300f)and (event.getY()<1500f))){
                    PAUSE = false
                    music.stopSound()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(context, intent, null)
                }
                if(start and !return_to_game){
                    if (!is_visible_bul[0] or !is_visible_bul[1] or !is_visible_bul[2]) {
                        if(is_visible_bul[2] == false ) {
                            is_shot[2] = true
                            is_visible_bul[2] = true
                            bullet_available -=1
                        }else if(is_visible_bul[1] == false) {
                            is_shot[1] = true
                            is_visible_bul[1] = true
                            bullet_available -=1
                        }else if(is_visible_bul[0] == false){
                            is_shot[0] = true
                            is_visible_bul[0] = true
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
                    up[0] = 0f
                    up[1] = 0f
                    up[2] = 0f
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
        if(!PAUSE) {
            ex_array[n] = false
            var delay_enemy = (3000L..8000L).random()
            delay(delay_enemy)
            var type = (0..2).random()
            var position = ((60..940).random())
            //Log.i("Prova", position.toString())
            for (i in (0..3)) {
                if (!enemy_visible[i]) {
                    enemy_visible[i] = true
                    array_type[i] = type
                    array_position[i] = position.toFloat()
                    break
                }
            }
        }
    }

    fun spawn_boss(n:Int){
        //var delay_enemy = (3000L..8000L).random()
        //delay(delay_enemy)
        //var type = (0..2).random()
        var position = ((100..990).random())
        //Log.i("Position", position.toString())
        boss_x[n] = position.toFloat()
        if(!boss_visible[n]){
            boss_visible[n] = true
            boss_x[n] = position.toFloat()
            shotboss[n]= true
        }
    }
}