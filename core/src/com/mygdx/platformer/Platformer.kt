package com.mygdx.platformer

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Platformer : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()
        setScreen(PlayScreen(this, "map.tmx"))
    }

    override fun dispose() {
        batch.dispose()
    }

    companion object {
        const val V_WIDTH = 480f
        const val V_HEIGHT = 300f
        const val GRAVITY = 10f
        const val PPM = 100f
        const val AWAKE_THRESHOLD: Float = 10f
    }
}
