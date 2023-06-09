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
    var collisionPlayer1 : MediaPlayer? = null               //Collision sound variable
    var collisionPlayer2 : MediaPlayer? = null               //Collision sound variable
    var collisionPlayer3 : MediaPlayer? = null               //Collision sound variable
    var hitPlayer1 : MediaPlayer? = null               //Collision sound variable
    var hitPlayer2 : MediaPlayer? = null               //Collision sound variable
    var hitPlayer3 : MediaPlayer? = null               //Collision sound variable
    var shootPlayer1 : MediaPlayer? = null               //Shoot bullet sound variable
    var shootPlayer2 : MediaPlayer? = null               //Shoot bullet sound variable
    var shootPlayer3 : MediaPlayer? = null               //Shoot bullet sound variable
    var choosePlayer1 : MediaPlayer? = null              //Choose sound variable
    var choosePlayer2 : MediaPlayer? = null              //Choose sound variable
    var choosePlayer3: MediaPlayer? = null              //Choose sound variable
    var hitBossPlayer1: MediaPlayer? = null                   //Hit boss sound variable
    var hitBossPlayer2: MediaPlayer? = null                   //Hit boss sound variable
    var finalExplosionBossPlayer: MediaPlayer? = null         //Boss death sound variable
    var finalExplosionPlayer: MediaPlayer? = null             //Gameover explosion sound variable
    var shootBossPlayer1 : MediaPlayer? = null               //Shoot bullet boss sound variable
    var shootBossPlayer2 : MediaPlayer? = null               //Shoot bullet boss sound variable
    var hitByBossPlayer1 : MediaPlayer? = null               //Hit by bullet boss sound variable
    var hitByBossPlayer2 : MediaPlayer? = null               //Hit by bullet boss sound variable


    var isplaying1 = false
    var isplaying2 = false
    var isplaying3 = false

    var isColliding1 = false
    var isColliding2 = false
    var isColliding3 = false

    var isShooting1 = false
    var isShooting2 = false
    var isShooting3 = false

    var isChoosing1 = false
    var isChoosing2 = false
    var isChoosing3 = false

    var isHitBoss1 = false
    var isHitBoss2 = false
    var isDeadBoss = false

    var isPlayingFinalExplosion = false

    var isShotBoss1 = false
    var isShotBoss2 = false

    var isHitByBoss1 = false
    var isHitByBoss2 = false



    fun playSoundMenu(conxt: Context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(conxt, R.raw.menumusiclow)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    fun playSoundGame(conxt: Context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(conxt, R.raw.gamemusiclow)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    fun playExplosionSound(conxt: Context) {
        if(!muted) {
            if (explosionPlayer1 == null) {
                explosionPlayer1 = MediaPlayer.create(conxt, R.raw.explosionsound)
                explosionPlayer1!!.start()
                isplaying1 = true
                explosionPlayer1!!.setOnCompletionListener() {
                    isplaying1 = false
                }
            } else if (!isplaying1) {
                explosionPlayer1!!.start()
                isplaying1 = true
                explosionPlayer1!!.setOnCompletionListener() {
                    isplaying1 = false
                }
            } else if (explosionPlayer2 == null) {
                explosionPlayer2 = MediaPlayer.create(conxt, R.raw.explosionsound)
                explosionPlayer2!!.start()
                isplaying2 = true
                explosionPlayer2!!.setOnCompletionListener {
                    isplaying2 = false
                }
            } else if (!isplaying2) {
                explosionPlayer2!!.start()
                isplaying2 = true
                explosionPlayer2!!.setOnCompletionListener() {
                    isplaying2 = false
                }
            } else if (explosionPlayer3 == null) {
                explosionPlayer3 = MediaPlayer.create(conxt, R.raw.explosionsound)
                explosionPlayer3!!.start()
                isplaying3 = true
                explosionPlayer3!!.setOnCompletionListener {
                    isplaying3 = false
                }
            } else if (!isplaying3) {
                explosionPlayer3!!.start()
                isplaying3 = true
                explosionPlayer3!!.setOnCompletionListener() {
                    isplaying3 = false
                }
            }
        }
    }

    fun playCollisionSound(conxt: Context) {
        if(!muted) {
            if (collisionPlayer1 == null) {
                collisionPlayer1 = MediaPlayer.create(conxt, R.raw.explosionsound)
                collisionPlayer1!!.start()
                hitPlayer1 = MediaPlayer.create(conxt, R.raw.hitsound)
                hitPlayer1!!.start()
                isColliding1 = true
                collisionPlayer1!!.setOnCompletionListener() {
                    isColliding1 = false
                }
            } else if (!isColliding1) {
                collisionPlayer1!!.start()
                hitPlayer1!!.start()
                isColliding1 = true
                collisionPlayer1!!.setOnCompletionListener() {
                    isColliding1 = false
                }
            } else if (collisionPlayer2 == null) {
                collisionPlayer2 = MediaPlayer.create(conxt, R.raw.explosionsound)
                collisionPlayer2!!.start()
                hitPlayer2 = MediaPlayer.create(conxt, R.raw.hitsound)
                hitPlayer2!!.start()
                isColliding2 = true
                collisionPlayer2!!.setOnCompletionListener() {
                    isColliding2 = false
                }
            } else if (!isColliding2) {
                collisionPlayer2!!.start()
                hitPlayer2!!.start()
                isColliding2 = true
                collisionPlayer2!!.setOnCompletionListener() {
                    isColliding2 = false
                }
            } else if (collisionPlayer3 == null) {
                collisionPlayer3 = MediaPlayer.create(conxt, R.raw.explosionsound)
                collisionPlayer3!!.start()
                hitPlayer3 = MediaPlayer.create(conxt, R.raw.hitsound)
                hitPlayer3!!.start()
                isColliding3 = true
                collisionPlayer3!!.setOnCompletionListener() {
                    isColliding3 = false
                }
            } else if (!isColliding3) {
                collisionPlayer3!!.start()
                hitPlayer3!!.start()
                isColliding3 = true
                collisionPlayer3!!.setOnCompletionListener() {
                    isColliding3 = false
                }
            }
        }
    }

    fun playShootSound(conxt: Context) {
        if(!muted) {
            if (shootPlayer1 == null) {
                shootPlayer1 = MediaPlayer.create(conxt, R.raw.shootsound)
                shootPlayer1!!.start()
                isShooting1 = true
                shootPlayer1!!.setOnCompletionListener() {
                    isShooting1 = false
                }
            } else if (!isShooting1) {
                shootPlayer1!!.start()
                isShooting1 = true
                shootPlayer1!!.setOnCompletionListener() {
                    isShooting1 = false
                }
            } else if (shootPlayer2 == null) {
                shootPlayer2 = MediaPlayer.create(conxt, R.raw.shootsound)
                shootPlayer2!!.start()
                isShooting2 = true
                shootPlayer2!!.setOnCompletionListener() {
                    isShooting2 = false
                }
            } else if (!isShooting2) {
                shootPlayer2!!.start()
                isShooting2 = true
                shootPlayer2!!.setOnCompletionListener() {
                    isShooting2 = false
                }
            } else if (shootPlayer3 == null) {
                shootPlayer3 = MediaPlayer.create(conxt, R.raw.shootsound)
                shootPlayer3!!.start()
                isShooting3 = true
                shootPlayer3!!.setOnCompletionListener() {
                    isShooting3 = false
                }
            } else if (!isShooting3) {
                shootPlayer3!!.start()
                isShooting3 = true
                shootPlayer3!!.setOnCompletionListener() {
                    isShooting3 = false
                }
            }
        }
    }

    fun playLaserSound(conxt: Context) {
        if(!muted) {
            if (shootPlayer1 == null) {
                shootPlayer1 = MediaPlayer.create(conxt, R.raw.lasersound)
                shootPlayer1!!.start()
                isShooting1 = true
                shootPlayer1!!.setOnCompletionListener() {
                    isShooting1 = false
                }
            } else if (!isShooting1) {
                shootPlayer1!!.start()
                isShooting1 = true
                shootPlayer1!!.setOnCompletionListener() {
                    isShooting1 = false
                }
            } else if (shootPlayer2 == null) {
                shootPlayer2 = MediaPlayer.create(conxt, R.raw.lasersound)
                shootPlayer2!!.start()
                isShooting2 = true
                shootPlayer2!!.setOnCompletionListener() {
                    isShooting2 = false
                }
            } else if (!isShooting2) {
                shootPlayer2!!.start()
                isShooting2 = true
                shootPlayer2!!.setOnCompletionListener() {
                    isShooting2 = false
                }
            } else if (shootPlayer3 == null) {
                shootPlayer3 = MediaPlayer.create(conxt, R.raw.lasersound)
                shootPlayer3!!.start()
                isShooting3 = true
                shootPlayer3!!.setOnCompletionListener() {
                    isShooting3 = false
                }
            } else if (!isShooting3) {
                shootPlayer3!!.start()
                isShooting3 = true
                shootPlayer3!!.setOnCompletionListener() {
                    isShooting3 = false
                }
            }
        }
    }

    fun playChooseSound(conxt: Context) {
        if(!muted) {
            if (choosePlayer1 == null) {
                choosePlayer1 = MediaPlayer.create(conxt, R.raw.choosesound)
                choosePlayer1!!.start()
                isChoosing1 = true
                choosePlayer1!!.setOnCompletionListener() {
                    isChoosing1 = false
                }
            } else if (!isChoosing1) {
                choosePlayer1!!.start()
                isChoosing1 = true
                choosePlayer1!!.setOnCompletionListener() {
                    isChoosing1 = false
                }
            } else if (choosePlayer2 == null) {
                choosePlayer2 = MediaPlayer.create(conxt, R.raw.choosesound)
                choosePlayer2!!.start()
                isChoosing2 = true
                choosePlayer2!!.setOnCompletionListener {
                    isChoosing2 = false
                }
            } else if (!isChoosing2) {
                choosePlayer2!!.start()
                isChoosing2 = true
                choosePlayer1!!.setOnCompletionListener() {
                    isChoosing2 = false
                }
            } else if (choosePlayer3 == null) {
                choosePlayer3 = MediaPlayer.create(conxt, R.raw.choosesound)
                choosePlayer3!!.start()
                isChoosing3 = true
                choosePlayer3!!.setOnCompletionListener {
                    isChoosing3 = false
                }
            } else if (!isChoosing3) {
                choosePlayer3!!.start()
                isChoosing3 = true
                choosePlayer3!!.setOnCompletionListener() {
                    isChoosing3 = false
                }
            }
        }
    }

    fun playHitBossSound(conxt: Context) {
        if(!muted) {
            if (hitBossPlayer1 == null) {
                hitBossPlayer1 = MediaPlayer.create(conxt, R.raw.bosshitsound)
                hitBossPlayer1!!.start()
                isHitBoss1 = true
                hitBossPlayer1!!.setOnCompletionListener() {
                    isHitBoss1 = false
                }
            } else if (!isHitBoss1) {
                hitBossPlayer1!!.start()
                isHitBoss1 = true
                hitBossPlayer1!!.setOnCompletionListener() {
                    isHitBoss1 = false
                }
            } else if (hitBossPlayer2 == null) {
                hitBossPlayer2 = MediaPlayer.create(conxt, R.raw.bosshitsound)
                hitBossPlayer2!!.start()
                isHitBoss2 = true
                hitBossPlayer2!!.setOnCompletionListener {
                    isHitBoss2 = false
                }
            } else if (!isHitBoss2) {
                hitBossPlayer2!!.start()
                isHitBoss2 = true
                hitBossPlayer2!!.setOnCompletionListener() {
                    isHitBoss2 = false
                }
            }
        }
    }

    fun playBossDeathSound(conxt: Context) {
        if(!muted) {
            if (finalExplosionBossPlayer == null) {
                finalExplosionBossPlayer = MediaPlayer.create(conxt, R.raw.explosionsound)
                finalExplosionBossPlayer!!.start()
                isDeadBoss = true
                finalExplosionBossPlayer!!.setOnCompletionListener() {
                    isDeadBoss = false
                }
            }
        }
    }

    fun playFinalExplosionSound(conxt: Context) {
        if(!muted) {
            if (finalExplosionPlayer == null) {
                finalExplosionPlayer = MediaPlayer.create(conxt, R.raw.explosionsound)
                finalExplosionPlayer!!.start()
                isPlayingFinalExplosion = true
                finalExplosionPlayer!!.setOnCompletionListener() {
                    isPlayingFinalExplosion = false
                }
            }
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


    fun playShootBossSound(conxt: Context) {
        if(!muted) {
            if (shootBossPlayer1 == null) {
                shootBossPlayer1 = MediaPlayer.create(conxt, R.raw.shootsound)
                shootBossPlayer1!!.start()
                isShotBoss1 = true
                shootBossPlayer1!!.setOnCompletionListener() {
                    isShotBoss1 = false
                }
            } else if (!isShooting1) {
                shootBossPlayer1!!.start()
                isShotBoss1 = true
                shootBossPlayer1!!.setOnCompletionListener() {
                    isShotBoss1 = false
                }
            } else if (shootBossPlayer2 == null) {
                shootBossPlayer2 = MediaPlayer.create(conxt, R.raw.shootsound)
                shootBossPlayer2!!.start()
                isShotBoss2 = true
                shootBossPlayer2!!.setOnCompletionListener() {
                    isShotBoss2 = false
                }
            } else if (!isShooting2) {
                shootBossPlayer2!!.start()
                isShotBoss2 = true
                shootBossPlayer2!!.setOnCompletionListener() {
                    isShotBoss2 = false
                }
            }
        }
    }

    fun playBulletBossCollisionSound(conxt: Context) {
        if(!muted) {
            if (collisionPlayer1 == null) {
                hitBossPlayer1 = MediaPlayer.create(conxt, R.raw.hitsound)
                hitBossPlayer1!!.start()
                isHitBoss1 = true
                hitBossPlayer1!!.setOnCompletionListener() {
                    isHitBoss1 = false
                }
            } else if (!isColliding1) {
                hitBossPlayer1!!.start()
                hitPlayer1!!.start()
                isHitBoss1 = true
                hitBossPlayer1!!.setOnCompletionListener() {
                    isHitBoss1 = false
                }
            } else if (collisionPlayer2 == null) {
                hitBossPlayer2 = MediaPlayer.create(conxt, R.raw.hitsound)
                hitBossPlayer2!!.start()
                isHitBoss2 = true
                hitBossPlayer2!!.setOnCompletionListener() {
                    isHitBoss2 = false
                }
            } else if (!isColliding2) {
                hitBossPlayer2!!.start()
                hitPlayer2!!.start()
                isHitBoss2 = true
                hitBossPlayer2!!.setOnCompletionListener() {
                    isHitBoss2 = false
                }
            }
        }
    }

    fun playGameoverMusic(conxt: Context) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(conxt, R.raw.gameovermusic)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }
}

