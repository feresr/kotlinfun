package com.mygdx.platformer.items

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.engine.Entity

/**
 * Created by feresr on 19/11/17.
 */
class Coin(x: Float, y: Float, onCollected: (Entity) -> Unit) : Sprite() {

    val body: Entity = Entity(Vector2(x, y), 11, 11)
    private var collected = false
    private val animation: Animation<TextureRegion>
    private var timer: Float = 0f

    init {

        //HeroAnimation
        val frames = com.badlogic.gdx.utils.Array<TextureRegion>(2)
        val ta = TextureAtlas("pill.pack")
        frames.add(TextureRegion(ta.findRegion("pill"), 0, 0, 11, 12))
        frames.add(TextureRegion(ta.findRegion("pill"), 11, 0, 11, 12))
        animation = Animation(.4f, frames, Animation.PlayMode.LOOP)

        body.gravity = 0f

        setBounds(x, y, animation.getKeyFrame(timer, true).regionWidth.toFloat(), animation.getKeyFrame(timer, true).regionHeight.toFloat())

        body.onUpdate = { dt ->
            timer += dt
            if (collected) {
                onCollected(body)
            }
        }
        body.userData = this
    }

    fun collect() {
        collected = true
    }

    override fun draw(batch: Batch?) {
        setRegion(animation.getKeyFrame(timer, true))
        super.draw(batch)
    }
}