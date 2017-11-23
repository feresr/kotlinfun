package com.mygdx.platformer.enemies

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.items.Coin
import com.mygdx.platformer.items.Lapse

class Immortal(x: Float,
               y: Float,
               onKilled: (Entity) -> Unit) : Sprite() {

    val body: Entity
    private val size = 20

    init {
        body = Entity(Vector2(x, y), size, size)
        body.velocity.x = -INITIAL_VELOCITY

        body.onUpdate = { if (body.position.y < 0) onKilled(body) }

        body.onCollisionLeft = { other ->
            val ud = other?.userData
            when (ud) {
                is Hero -> ud.kill()
                is Lapse, is Coin -> {
                }
                else -> {
                    if (other is Entity) body.position.x = other.position.x + other.width
                    body.velocity.x = INITIAL_VELOCITY
                }
            }
        }

        body.onCollisionRight = { other ->
            val ud = other?.userData
            when (ud) {
                is Hero -> ud.kill()
                is Lapse, is Coin -> {
                }
                else -> {
                    if (other is Entity) body.position.x = other.position.x - body.width
                    body.velocity.x = -INITIAL_VELOCITY
                }
            }
        }
        body.userData = this
    }


    companion object {
        const val INITIAL_VELOCITY = .5f
    }
}