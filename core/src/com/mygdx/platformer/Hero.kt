package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.enemies.Enemy
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.Sensor

class Hero(x: Float, y: Float, world: (Entity) -> Unit,
           private val onDoorEntered: (Door) -> Unit,
           private val onKilled: () -> Unit) : Sprite() {

    val body: Entity
    private var canJump = false
    private var facingRight = true

    private val idleAnimation: Animation<TextureRegion>
    private var timer = 0f

    init {
        setBounds(x, y, 11f, 23f)

        val feetSensor = Sensor(Rectangle(1f, -2f, 9f, 4f), this::onHeroStepsOn)
        val bodySensor = Sensor(Rectangle(0f, 0f, 11f, 23f), this::onBodySensor)
        body = Entity(Vector2(x, y), 11, 23, arrayOf(feetSensor, bodySensor))

        body.onUpdate = this::update

        val frames = com.badlogic.gdx.utils.Array<TextureRegion>(2)
        val ta = TextureAtlas("hero.pack")
        frames.add(TextureRegion(ta.findRegion("hero"), 0, 0, 11, 23))
        frames.add(TextureRegion(ta.findRegion("hero"), 0, 23, 11, 23))
        idleAnimation = Animation(.4f, frames, Animation.PlayMode.LOOP)

        body.onCollisionBottom = { other ->
            canJump = true
            other?.let {
                if (other is Entity) {
                    when (other.userData) {
                        is Box -> {
                            body.velocity.y = 0f
                            body.position.y = other.position.y + other.height
                        }
                    }
                }
            }
        }

        body.onCollisionLeft = { other ->
            other?.let {
                when ((other as Entity).userData) {
                    is Box -> {
                        body.velocity.x = 0f
                        body.position.x = other.position.x + other.width
                    }
                }
            }
        }

        body.onCollisionRight = { other ->
            other?.let {
                when ((other as Entity).userData) {
                    is Box -> {
                        body.velocity.x = 0f
                        body.position.x = other.position.x - body.width
                    }
                }
            }
        }

        body.userData = this
        body.isActive = true
        world(body)
    }

    fun dispose() {
    }

    private fun moveRight() {
        facingRight = true
        body.velocity.x = Hero.SPEED
    }

    private fun moveLeft() {
        facingRight = false
        body.velocity.x = -Hero.SPEED
    }

    fun update(delta: Float) {
        //body.linearVelocity = vector.set(0f, body.linearVelocity.y)

        timer += delta

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && timer > .1f) {
            timer = 0f
            //body.gravity = 0f
            shadow()
        }
        if (timer > .1) {
            body.isVisible = true
            body.gravity = 1f
            body.velocity.x = 0f


            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                moveRight()
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                jump()
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                moveLeft()
            }
        }


    }

    override fun draw(batch: Batch?) {
        setPosition(body.position.x, body.position.y)
        batch?.draw(idleAnimation.getKeyFrame(timer, true), if (!facingRight) x + width else x, y, if (!facingRight) -width else width, height);
    }

    private fun shadow() {
        body.isVisible = false
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.velocity.x = -20f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.velocity.x = 20f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) { //JUMP
            body.velocity.y = 10f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.velocity.y = -20f
        }
    }

    private fun onHeroStepsOn(any: Any?) {
        ((any as? Entity)?.userData as? Enemy)?.let {
            if (body.isVisible) {
                body.velocity.y = JUMP_SPEED
            }
            it.die()
        }
    }

    fun kill() {
        onKilled()
    }

    private fun onBodySensor(entity: Any?) {
        (entity as? Entity)?.let {
            it.userData?.let {
                when (it) {
                    is Door -> {
                        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) { //JUMP
                            onDoorEntered(it)
                        }
                    }
                }
            }
        }
    }

    private fun jump() {
        if (canJump && body.velocity.y == 0f) {
            body.velocity.y = JUMP_SPEED
            canJump = false
        }
    }

    companion object {
        const val SPEED = 2f
        const val JUMP_SPEED = 10f
    }
}