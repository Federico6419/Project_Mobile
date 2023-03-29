package project.mobile

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

//Music management class
public class MusicManager {

    var mMediaPlayer: MediaPlayer? = null                   //Music variable
    var explosionPlayer1 : MediaPlayer? = null               //Explosion sound variable
    var explosionPlayer2 : MediaPlayer? = null               //Explosion sound variable
    var explosionPlayer3 : MediaPlayer? = null               //Explosion sound variable

    var isplaying1 = false
    var isplaying2 = false
    var isplaying3 = false

    fun playSoundMenu(conxt: Context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(conxt, R.raw.menumusic)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    fun playSoundGame(conxt: Context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(conxt, R.raw.gamemusic)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    fun playExplosionSound(conxt: Context) {
        if (explosionPlayer1 == null) {
            explosionPlayer1 = MediaPlayer.create(conxt, R.raw.explosionsound)
            explosionPlayer1!!.start()
            isplaying1 = true
            explosionPlayer1!!.setOnCompletionListener(){
                isplaying1 = false
            }
        }
        else if(!isplaying1){
                explosionPlayer1!!.start()
                isplaying1 = true
                explosionPlayer1!!.setOnCompletionListener(){
                    isplaying1 = false
                }
            }
        else if(explosionPlayer2 == null) {
            explosionPlayer2 = MediaPlayer.create(conxt, R.raw.explosionsound)
            explosionPlayer2!!.start()
            isplaying2 = true
            explosionPlayer2!!.setOnCompletionListener {
                isplaying2 = false
            }
        }
        else if(!isplaying2){
            explosionPlayer2!!.start()
            isplaying2 = true
            explosionPlayer1!!.setOnCompletionListener(){
                isplaying2 = false
            }
        } else if(explosionPlayer3 == null) {
            explosionPlayer3 = MediaPlayer.create(conxt, R.raw.explosionsound)
            explosionPlayer3!!.start()
            isplaying3 = true
            explosionPlayer3!!.setOnCompletionListener{
                isplaying3 = false
            }
        }
        else if(!isplaying3){
            explosionPlayer3!!.start()
            isplaying3 = true
            explosionPlayer3!!.setOnCompletionListener(){
                isplaying3 = false
            }
        }
    }

    fun completion() {
        Log.i("FINISHED", "OK")
    }

    fun pauseSound() {
        if (mMediaPlayer?.isPlaying == true) mMediaPlayer?.pause()
    }

    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    fun setVolume(left: Float, right: Float){
        mMediaPlayer?.setVolume(left, right)
    }

    fun releasePlayer(){
        mMediaPlayer?.release()
    }
}

