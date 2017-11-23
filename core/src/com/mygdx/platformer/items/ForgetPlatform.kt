package com.mygdx.platformer.items

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Entity

class ForgetPlatform(x: Float, y: Float, width: Int, height: Int) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), width, height)

    init {
        body.userData = this
        body.gravity = 0f
    }
}