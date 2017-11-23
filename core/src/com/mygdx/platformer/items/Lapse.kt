package com.mygdx.platformer.items

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.platformer.Hero
import com.mygdx.platformer.engine.Entity
import com.mygdx.platformer.engine.Sensor

/**
 * Created by feresr on 22/11/17.
 */
class Lapse(x: Float,
            y: Float, width: Float, height: Float) : Sprite(Texture("lapse.png")) {


    val body: Entity

    init {

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

        val sensor = Sensor(Rectangle(0f, 0f, width, height),
                { entity ->
                    val ud = entity.userData
                    when (ud) {
                        is Hero -> {
                            ud.onEnterLapse()
                            ud.body.gravity = 0.1f
                            ud.body.velocity.y *= .97f
                            ud.body.velocity.x *= .4f
                        }
                    }
                })

        val sensors = arrayOf(sensor)

        body = Entity(Vector2(x, y), width.toInt(), height.toInt(), sensors)

        body.gravity = 0f
        body.userData = this
    }

    override fun draw(batch: Batch) {
        val currentShader = batch.shader
        batch.shader = null
        batch.draw(texture, body.position.x, body.position.y, width.toInt(), height.toInt(), body.width, body.height)
        batch.shader = currentShader
    }
}