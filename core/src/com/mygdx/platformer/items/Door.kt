package com.mygdx.platformer.items

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Entity

/**
 * Created by feresr on 16/11/17.
 */
class Door(x: Float, y: Float, val level: String) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), 16, 16)

    init {
        body.userData = this
    }
}