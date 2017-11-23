package com.mygdx.platformer.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity

class Box(x: Float, y: Float, width: Int, height: Int) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), width, height)
    private var beingPushedFromRight = false
    private var beingPushedFromLeft = false

    init {
        body.userData = this

        body.onCollisionLeft = { entity ->
            when (entity?.userData) {
                is Hero -> beingPushedFromLeft = true
                is Box -> {
                    body.velocity.x = 0f
                    entity.velocity.x = 0f
                    body.position.x = entity.position.x + entity.width
                }
            }
        }

        body.onCollisionRight = { entity ->
            when (entity?.userData) {
                is Hero -> beingPushedFromRight = true
                is Box -> {
                    body.velocity.x = 0f
                    entity.velocity.x = 0f
                    body.position.x = entity.position.x - body.width
                }
            }
        }

        body.onCollisionBottom = { entity ->
            (entity?.userData as? Box)?.let {
                body.velocity.y = 0f
                it.body.velocity.y = 0f
                body.position.y = it.body.position.y + it.body.height
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
    }

    companion object {
        const val WEIGHT = .3f
    }

}