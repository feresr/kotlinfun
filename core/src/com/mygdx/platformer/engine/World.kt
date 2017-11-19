package com.mygdx.platformer.engine

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle

/**
 * Created by feresr on 15/11/17.
 */
class World(private val tiles: TiledMapTileLayer) {

    private val shapeRenderer: ShapeRenderer by lazy { ShapeRenderer() }
    private val entities: ArrayList<Entity> = ArrayList()

    private val TILESIZE = 16

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    private fun stepX(entity: Entity) {

        val fromTileIndexY = (entity.position.y / TILESIZE).toInt()
        val toTileIndexY = ((entity.position.y + (entity.height - 1)) / TILESIZE).toInt() // -1 checks the case where the player is exactly X tiles height, and has to go through a X tiles opening

        val facingX = if (entity.velocity.x > 0f) entity.position.x + entity.width else entity.position.x
        val targetFacingX = facingX + entity.velocity.x

        val tileIndexX = (facingX / TILESIZE).toInt()
        val targetTileIndexX = (targetFacingX / TILESIZE).toInt()

        if (entity.velocity.x > 0) {
            //RIGHT
            for (x in tileIndexX..targetTileIndexX) {
                for (y in fromTileIndexY..toTileIndexY) {
                    val tile = tiles.getCell(x, y)?.tile
                    if (tile != null) {
                        if (TILESIZE * x.toFloat() <= targetFacingX) {
                            entity.position.x = TILESIZE * x.toFloat() - entity.width
                            entity.velocity.x = 0f
                            entity.onCollisionRight?.invoke(null)
                        } else {
                            entity.position.x = entity.position.x + entity.velocity.x
                        }

                        return
                    }
                }
            }
        } else {
            //LEFT
            for (x in tileIndexX.downTo(targetTileIndexX)) {
                for (y in fromTileIndexY..toTileIndexY) {
                    val tile = tiles.getCell(x, y)?.tile

                    if (tile != null) {
                        if (TILESIZE * x.toFloat() + TILESIZE >= targetFacingX) {
                            entity.position.x = TILESIZE * x.toFloat() + TILESIZE
                            //Gdx.app.log("collision", "left of enemy")
                            entity.velocity.x = 0f
                            entity.onCollisionLeft?.invoke(null)
                        } else {
                            entity.position.x = entity.position.x + entity.velocity.x
                        }

                        return
                    }
                }
            }
        }


        entity.position.x = entity.position.x + entity.velocity.x
    }

    private fun stepY(entity: Entity) {

        val fromTileIndexX = (entity.position.x / TILESIZE).toInt()
        val toTileIndexX = ((entity.position.x + (entity.width - 1)) / TILESIZE).toInt() // -1 checks the case where the player is exactly X tiles height, and has to go through a X tiles opening

        val facingY = if (entity.velocity.y > 0f) entity.position.y + entity.height else entity.position.y
        val targetFacingY = facingY + entity.velocity.y

        val tileIndexY = (facingY / TILESIZE).toInt()
        val targetTileIndexY = (targetFacingY / TILESIZE).toInt()

        if (entity.velocity.y > 0) {
            for (y in tileIndexY..targetTileIndexY) {
                for (x in fromTileIndexX..toTileIndexX) {
                    val tile = tiles.getCell(x, y)?.tile
                    if (tile != null) {
                        if (TILESIZE * y.toFloat() <= targetFacingY) {
                            entity.position.y = TILESIZE * y.toFloat() - entity.height
                            entity.velocity.y = 0f
                            entity.onCollisionTop?.invoke(null)
                        } else {
                            entity.position.y = entity.position.y + entity.velocity.y
                        }

                        return
                    }
                }
            }
        } else {
            for (y in tileIndexY.downTo(targetTileIndexY)) {
                for (x in fromTileIndexX..toTileIndexX) {
                    val tile = tiles.getCell(x, y)?.tile

                    if (tile != null) {
                        if (TILESIZE * y.toFloat() + TILESIZE >= targetFacingY) {
                            entity.position.y = TILESIZE * y.toFloat() + TILESIZE
                            entity.velocity.y = 0f
                            entity.onCollisionBottom?.invoke(null)
                        } else {
                            entity.position.y = entity.position.y + entity.velocity.y
                        }

                        return
                    }
                }
            }
        }

        entity.position.y = entity.position.y + entity.velocity.y
    }

    val GRAVITY = .5f
    val MAX_FALL_SPEED = 12f


    fun step(delta: Float) {
        entities.filter { it.isActive }
                .forEach { it.onUpdate?.invoke(delta) }

        //Tiles collisions
        for (entity in entities) {
            if (entity.isActive) {
                if (entity.velocity.y > -MAX_FALL_SPEED) {
                    entity.velocity.y -= entity.gravity * GRAVITY
                }
                if (entity.velocity.x != 0f) stepX(entity)
                if (entity.velocity.y != 0f) stepY(entity)
            }
        }

        //Triggers
        //the order of the collisions matters here, it is recommended for the player
        //to be checked first. When jumping on top of a goomba it will set its entity as not 'active
        //and the goomba check wont't be performed preventing hurting the player
        entities.asSequence()
                .filter { it.isActive && it.sensors.isNotEmpty() }
                .forEach { entity ->
                    entities
                            .asSequence()
                            .filter { it.isActive && it != entity }
                            .forEach { checkCollisions(entity, it) }
                }

        //AABB collisions
        entities.asSequence()
                .filter { it.isActive && (it.velocity.len2() > 0f) }
                .forEach { entity ->
                    entities.asSequence()
                            .filter { other -> other.isActive && other != entity }
                            .forEach { other ->
                                val rect1 = Rectangle(entity.position.x, entity.position.y, entity.width.toFloat(), entity.height.toFloat())
                                val rect2 = Rectangle(other.position.x, other.position.y, other.width.toFloat(), other.height.toFloat())

                                val A: Rectangle = rect1
                                val B: Rectangle = rect2

                                //if (A.overlaps(B)) {
                                val w = .5 * (A.width + B.width)
                                val h = .5 * (A.height + B.height)
                                val dx = (A.x + A.width / 2f) - (B.x + B.width / 2f)
                                val dy = (A.y + A.height / 2f) - (B.y + B.height / 2f)

                                if (Math.abs(dx) <= w && Math.abs(dy) <= h) {
                                    /* collision! */

                                    entity.onCollision?.invoke(other)
                                    other.onCollision?.invoke(entity)

                                    val wy = w * dy
                                    val hx = h * dx

                                    if (wy > hx) {
                                        if (wy > -hx) {
                                            //Bottom
                                            entity.onCollisionBottom?.invoke(other)
                                            other.onCollisionTop?.invoke(entity)
                                        } else {
                                            //Right
                                            entity.onCollisionRight?.invoke(other)
                                            other.onCollisionLeft?.invoke(entity)
                                        }
                                    } else {
                                        if (wy > -hx) {
                                            //Left
                                            entity.onCollisionLeft?.invoke(other)
                                            other.onCollisionRight?.invoke(entity)
                                        } else {
                                            //Top
                                            entity.onCollisionTop?.invoke(other)
                                            other.onCollisionBottom?.invoke(entity)
                                        }
                                    }
                                }
                                //}
                            }
                }


    }

    private fun checkCollisions(entity: Entity, other: Entity) {
        if (entity.position.dst(other.position) < TILESIZE * 5) {
            val rec2 = Rectangle(other.position.x, other.position.y, other.width.toFloat(), other.height.toFloat())
            for (sensor in entity.sensors) {
                if (rec2.overlaps(Rectangle(entity.position.x + sensor.rectangle.x, entity.position.y + sensor.rectangle.y, sensor.rectangle.width, sensor.rectangle.height))) {
                    sensor.f(other)
                }
            }
        }
    }

    fun render(pm: Matrix4) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.projectionMatrix = pm

        for (entity in entities) {

            if (!entity.isVisible) continue

            shapeRenderer.setColor(1f, 0f, 0f, 1f);
            shapeRenderer.rect(entity.position.x,
                    entity.position.y,
                    entity.width.toFloat(),
                    entity.height.toFloat())
            shapeRenderer.setColor(0f, 1f, 0f, 1f);
            for (sensor in entity.sensors) {
                shapeRenderer.rect(entity.position.x + sensor.rectangle.x, entity.position.y + sensor.rectangle.y, sensor.rectangle.width.toFloat(), sensor.rectangle.height.toFloat())
            }
        }
        shapeRenderer.end()
    }

}
