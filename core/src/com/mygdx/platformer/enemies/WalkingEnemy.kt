package com.mygdx.platformer.enemies

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.mygdx.platformer.Hero
import com.mygdx.platformer.Platformer
import com.mygdx.platformer.toMeters

/**
 * Created by feresr on 13/11/17.
 */
class WalkingEnemy(x: Float, y: Float, world: (BodyDef) -> Body) {


    private val velocity: Vector2 = Vector2(-1f, 0f)
    private val feetWidth = 2f
    private val bodySize = 8f
    private val body2FeetDistance = 1f

    private final fun buildFeet(fdef: FixtureDef) {
        val edgeshapre = EdgeShape()
        edgeshapre.set(com.badlogic.gdx.math.Vector2((-feetWidth).toMeters(), (-bodySize).toMeters() - body2FeetDistance.toMeters()),
                Vector2((feetWidth).toMeters(), (-bodySize).toMeters() - body2FeetDistance.toMeters()))
        fdef.shape = edgeshapre
        //body.createFixture(fdef)
    }

    init {
        val fdef = FixtureDef()

        buildFeet(fdef)

        //body
        val shape = CircleShape()
        shape.radius = bodySize.toMeters()
        fdef.shape = shape
        fdef.isSensor = false

        //body.createFixture(fdef).userData = CollisionEvent(this, this@WalkingEnemy::onCollision)

        //body.isFixedRotation = true
        //left contact
        /*val edgeshapre = EdgeShape()

        edgeshapre.set(Vector2(-bodySize.toMeters() - 2f.toMeters(), 2f.toMeters()), Vector2(-bodySize.toMeters() - 2f.toMeters(), (-2f).toMeters()))
        fdef.shape = edgeshapre
        fdef.isSensor = true

        body.createFixture(fdef).apply {
            userData = CollisionEvent(this, this@WalkingEnemy::onCollision)
        }

        //right contact
        edgeshapre.set(Vector2(bodySize.toMeters() + 2f.toMeters(), 2f.toMeters()), Vector2(bodySize.toMeters() + 2f.toMeters(), (-2f).toMeters()))
        fdef.shape = edgeshapre
        fdef.isSensor = true

        body.createFixture(fdef).apply {
            userData = CollisionEvent(this, this@WalkingEnemy::onCollision)
        }*/

        //head
        val head = PolygonShape()
        val vertice = arrayOfNulls<Vector2>(4)

        vertice[0] = Vector2(-bodySize + 2f, bodySize + 4f).scl(1 / Platformer.PPM)
        vertice[1] = Vector2(bodySize - 2f, bodySize + 4f).scl(1 / Platformer.PPM)
        vertice[2] = Vector2(-3f, 3f).scl(1 / Platformer.PPM)
        vertice[3] = Vector2(3f, 3f).scl(1 / Platformer.PPM)

        head.set(vertice)
        fdef.shape = head
        fdef.isSensor = true


        //body.createFixture(fdef).userData = CollisionEvent(this, this::onHeadHit)
    }

    private fun onHeadHit(any: Any?) {
        if (any != null && any is Hero) {
            /*if (!isDead && any.body.linearVelocity.y < 0) {
                any.invertSpeed(false, true)
            }*/
            //isDead = true

        }
    }

/*    override fun update(delta: Float) {
        super.update(delta)
        velocity.y = body.linearVelocity.y
        body.linearVelocity = velocity
    }*/

    private fun onCollision(any: Any?) {
        //if (any is Hero) return
        velocity.x *= -1
    }
}


/**
override var dead: Boolean = false

override val velocity: Vector2 = Vector2(-1f, 0f)

override fun update(delta: Float) {
walk(delta)
}

override fun onBuildFixtures() {

}

 *
 * */