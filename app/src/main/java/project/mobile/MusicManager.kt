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
            explosionPlayer1!!.isLooping = false
            explosionPlayer1!!.start()
            explosionPlayer1!!.setOnCompletionListener{
                //explosionPlayer1 = null
                Log.i("FINISHED", "OK")
            }
        } else if(explosionPlayer2 == null) {
            explosionPlayer2 = MediaPlayer.create(conxt, R.raw.explosionsound)
            explosionPlayer2!!.isLooping = false
            explosionPlayer2!!.start()
            /*explosionPlayer2!!.setOnCompletionListener{
                Log.i("FINISHED", "OK")
            }*/
        }else if(explosionPlayer3 == null) {
            explosionPlayer3 = MediaPlayer.create(conxt, R.raw.explosionsound)
            explosionPlayer3!!.isLooping = false
            explosionPlayer3!!.start()
            /*explosionPlayer3!!.setOnCompletionListener{
                Log.i("FINISHED", "OK")
            }*/
        }
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