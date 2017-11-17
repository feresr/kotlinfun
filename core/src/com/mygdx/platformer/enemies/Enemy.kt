package com.mygdx.platformer.enemies

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.Sensor

/**
 * Created by feresr on 13/11/17.
 */
class Enemy(x: Float, y: Float, world: (Entity) -> Unit) : Sprite() {

    val body: Entity
    var isDead: Boolean = false
    private val size = 16
    val position: Vector2
        get() = body.position

    init {
        val sensor = Sensor(Rectangle(0f, 0f, size.toFloat(), 14f), this::onCollision)
        body = Entity(Vector2(x, y), size, size, arrayOf(sensor))
        body.velocity.x = 1f
        body.onCollisionLeft = { body.velocity.x = 1f }
        body.onCollisionRight = { body.velocity.x = -1f }
        body.userData = this
        world(body)
    }

    private fun onCollision(any: Any?) {
        if (any is Hero) {
            any.kill()
        }

        if (any is Enemy) {
            body.velocity.x *= -1
        }
    }

    fun die() {
        isDead = true
        body.isActive = false
    }

    fun setActive(active: Boolean) {
        body.isActive = active
    }
}