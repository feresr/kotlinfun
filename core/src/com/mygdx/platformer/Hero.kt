package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.enemies.Enemy
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.Sensor

/**
 * Created by feresr on 13/11/17.
 */
class Hero(x: Float, y: Float, world: (Entity) -> Unit,
           private val onDoorEntered: (Door) -> Unit,
           private val onKilled: () -> Unit) : Sprite() {

    val body: Entity
    private var canJump = false

    init {
        val feetSensor = Sensor(Rectangle(1f, -2f, 14f, 2f), this::onHeroStepsOn)
        val bodySensor = Sensor(Rectangle(0f, 0f, 16f, 16f), this::onHurt)
        body = Entity(Vector2(x, y), 16, 16, arrayOf(feetSensor, bodySensor))

        body.onUpdate = this::update
        //body.onCollisionTop = { body.velocity.y = 2f}
        body.onCollisionBottom = { canJump = true }
        body.userData = this
        body.isActive = true
        world(body)
    }

    fun dispose() {

    }

    private fun moveRight() {
        body.velocity.x = Hero.SPEED
    }

    private fun moveLeft() {
        body.velocity.x = -Hero.SPEED
    }

    private fun moveUp() {
        body.velocity.y = Hero.SPEED
    }

    private fun moveDown() {
        body.velocity.y = -Hero.SPEED
    }

    fun update(delta: Float) {
        //body.linearVelocity = vector.set(0f, body.linearVelocity.y)
        body.velocity.x = 0f

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveRight()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) { //JUMP
            jump()
            //moveUp()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveLeft()
        }
    }

    private fun onHeroStepsOn(entity: Any?) {

        if (entity is Enemy) {
            body.velocity.y = 12f
            entity.die()
        }
    }

    public fun kill() {
        onKilled()
    }

    private fun onHurt(entity: Any?) {
        when (entity) {
            is Door -> {
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) { //JUMP
                    onDoorEntered(entity)
                }
            }
        }
    }

    private fun onCollision(any: Any?) {
        if (any is Enemy) {

        }
    }

    fun invertSpeed(x: Boolean, y: Boolean) {
        if (x) {
            //vector.set(-body.linearVelocity.x, body.linearVelocity.y)
            //body.linearVelocity = vector
        }

        if (y) {
            //vector.set(body.linearVelocity.x, -body.linearVelocity.y)
            //body.linearVelocity = vector
        }
    }

    private fun jump(speed: Float = Hero.JUMP_SPEED) {
        if (canJump) {
            body.velocity.y = JUMP_SPEED
            canJump = false
        }
    }

    companion object {
        const val SIZE = 8f
        const val SPEED = 3f
        const val JUMP_SPEED = 10f
    }
}