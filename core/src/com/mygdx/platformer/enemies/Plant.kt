package com.mygdx.platformer.enemies

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.Sensor


/**
 * Created by feresr on 14/11/17.
 */
class Plant(x: Float, y: Float, world: (Entity) -> Unit) {

    private val plantHeight: Float = 30f
    private val plantWidth: Float = 12f
    private var timer: Float = 0f
    private val upDownTime: Float = 2.2f
    private val growHideTime: Float = 1f

    private val growSpeed: Float = 4f
    private val hideSpeed: Float = 1f

    private val originalY: Float = y

    private enum class State { UP, GROWING, DOWN, HIDING }

    private val body: Entity
    private var state: State = State.DOWN

    init {
        val sensor = Sensor(Rectangle(0f, -plantHeight, plantWidth, plantHeight), this::onCollision)
        body = Entity(Vector2(x - plantWidth / 2, y), 0, 0, arrayOf(sensor))
        body.onUpdate = this::update
        body.isActive = true
        body.gravity = 0f
        world(body)
    }

    private fun onCollision(any: Any?) {
        ((any as? Entity)?.userData as? Hero)?.kill()
    }

    private fun update(delta: Float) {

        timer += delta
        when (state) {
            State.DOWN -> {
                if (timer > upDownTime) {
                    timer = 0f
                    state = State.GROWING
                }
            }
            State.GROWING -> {
                if (timer > growHideTime) { //time's up, next state
                    timer = 0f
                    state = State.UP
                    body.velocity.y = 0f
                } else {
                    if (body.position.y <= originalY + plantHeight) { // only continue to grow if it hasn't reach it maximum height
                        body.velocity.y = growSpeed
                    } else {
                        body.velocity.y = 0f
                    }
                }
            }
            State.UP -> {
                if (timer > upDownTime) {
                    timer = 0f
                    state = State.HIDING
                }
            }
            State.HIDING -> {
                if (timer > growHideTime) {
                    timer = 0f
                    state = State.DOWN
                    body.velocity.y = 0f
                } else {
                    if (body.position.y >= originalY) {
                        body.velocity.y = -hideSpeed
                    } else {
                        body.velocity.y = 0f
                    }
                }
            }
        }
    }
}