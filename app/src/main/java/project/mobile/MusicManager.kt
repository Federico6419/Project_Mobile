package project.mobile

import android.content.Context
import android.media.MediaPlayer

//Music management class
public class MusicManager {

    var mMediaPlayer: MediaPlayer? = null                   //Music variable

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
}
