package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Entity

class Box(x: Float, y: Float, width: Int, height: Int, world: (Entity) -> Unit) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), width, height)
    private var beingPushedFromRight = false
    private var beingPushedFromLeft = false

    init {
        body.isActive = true
        body.userData = this

        body.onCollisionLeft = { other ->
            (other as? Entity)?.let { entity ->
                (entity.userData as? Hero)?.let {
                    beingPushedFromLeft = true
                }

                (entity.userData as? Box)?.let {
                    body.velocity.x = 0f
                    entity.velocity.x = 0f
                    body.position.x = entity.position.x + entity.width
                }
            }
        }

        body.onCollisionRight = { other ->
            (other as? Entity)?.let { entity ->
                (entity.userData as? Hero)?.let {
                    beingPushedFromRight = true
                }

                (entity.userData as? Box)?.let {
                    body.velocity.x = 0f
                    entity.velocity.x = 0f
                    body.position.x = entity.position.x - body.width
                }
            }
        }

        body.onCollisionBottom = { other ->
            (other as? Entity)?.let { entity ->
                (entity.userData as? Box)?.let {
                    body.velocity.y = 0f
                    entity.velocity.y = 0f
                    body.position.y = entity.position.y + entity.height
                }
            }
        }

        body.onUpdate = {

            body.velocity.x = 0f

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                beingPushedFromLeft = false
                if (beingPushedFromRight) {
                    body.velocity.x = -(Hero.SPEED * WEIGHT)
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                beingPushedFromRight = false
                if (beingPushedFromLeft) {
                    body.velocity.x = (Hero.SPEED * WEIGHT)
                }
            }

            beingPushedFromLeft = false
            beingPushedFromRight = false
        }

        world(body)
    }

    companion object {
        const val WEIGHT = .3f
    }

}