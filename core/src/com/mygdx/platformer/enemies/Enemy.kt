package com.mygdx.platformer.enemies

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity

class Enemy(x: Float,
            y: Float,
            onKilled: (Entity) -> Unit) : Sprite() {

    val body: Entity
    var isDead: Boolean = false
    private val size = 16

    init {
        body = Entity(Vector2(x, y), size, size)
        body.velocity.x = -INITIAL_VELOCITY

        body.onUpdate = { if (isDead || body.position.y < 0) onKilled(body) }

        body.onCollisionLeft = { other ->
            onCollision(other)
            if (other is Entity) body.position.x = other.position.x + other.width
            body.velocity.x = INITIAL_VELOCITY
        }

        body.onCollisionRight = { other ->
            onCollision(other)
            if (other is Entity) body.position.x = other.position.x - body.width
            body.velocity.x = -INITIAL_VELOCITY
        }

        body.userData = this
    }

    private fun onCollision(any: Any?) {
        ((any as? Entity)?.userData as? Hero)?.kill()
    }

    fun die() {
        isDead = true
    }

    companion object {
        const val INITIAL_VELOCITY = .8f
    }
}