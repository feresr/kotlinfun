package com.mygdx.platformer.items

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Entity

/**
 * Created by feresr on 19/11/17.
 */
class Coin(x: Float, y: Float, onCollected: (Entity) -> Unit) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), 16, 16)
    private var collected = false

    init {
        body.gravity = 0f
        body.onUpdate = {
            if (collected) {
                onCollected(body)
            }
        }
        body.userData = this
    }

    fun collect() {
        collected = true
    }
}