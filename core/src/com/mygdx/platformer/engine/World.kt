package com.mygdx.platformer.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle

/**
 * Created by feresr on 15/11/17.
 */
class World(private val tiles: TiledMapTileLayer, private val gravity: Float) {

    private val shapeRenderer: ShapeRenderer by lazy { ShapeRenderer() }
    private val entities: ArrayList<Entity> = ArrayList()

    fun addEntity(entity: Entity) {
        entities.add(entity)
    }

    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    private fun stepX(entity: Entity) {

        val fromTileIndexY = (entity.position.y / TILE_SIZE).toInt()
        val toTileIndexY = ((entity.position.y + (entity.height - 1)) / TILE_SIZE).toInt() // -1 checks the case where the player is exactly X tiles height, and has to go through a X tiles opening

        val facingX = if (entity.velocity.x > 0f) entity.position.x + entity.width else entity.position.x
        val targetFacingX = facingX + entity.velocity.x

        val tileIndexX = (facingX / TILE_SIZE).toInt()
        val targetTileIndexX = (targetFacingX / TILE_SIZE).toInt()

        if (entity.velocity.x > 0) {
            //RIGHT
            for (x in tileIndexX..targetTileIndexX) {
                for (y in fromTileIndexY..toTileIndexY) {
                    val tile = tiles.getCell(x, y)?.tile
                    if (tile != null) {
                        if (TILE_SIZE * x.toFloat() <= targetFacingX) {
                            entity.position.x = TILE_SIZE * x.toFloat() - entity.width
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
                        if (TILE_SIZE * x.toFloat() + TILE_SIZE >= targetFacingX) {
                            entity.position.x = TILE_SIZE * x.toFloat() + TILE_SIZE
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

        val fromTileIndexX = (entity.position.x / TILE_SIZE).toInt()
        val toTileIndexX = ((entity.position.x + (entity.width - 1)) / TILE_SIZE).toInt() // -1 checks the case where the player is exactly X tiles height, and has to go through a X tiles opening

        val facingY = if (entity.velocity.y > 0f) entity.position.y + entity.height else entity.position.y
        val targetFacingY = facingY + entity.velocity.y

        val tileIndexY = (facingY / TILE_SIZE).toInt()
        val targetTileIndexY = (targetFacingY / TILE_SIZE).toInt()

        if (entity.velocity.y > 0) {
            for (y in tileIndexY..targetTileIndexY) {
                for (x in fromTileIndexX..toTileIndexX) {
                    val tile = tiles.getCell(x, y)?.tile
                    if (tile != null) {
                        if (TILE_SIZE * y.toFloat() <= targetFacingY) {
                            entity.position.y = TILE_SIZE * y.toFloat() - entity.height
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
                        if (TILE_SIZE * y.toFloat() + TILE_SIZE >= targetFacingY) {
                            entity.position.y = TILE_SIZE * y.toFloat() + TILE_SIZE
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

    fun step(delta: Float) {
        Gdx.app.log("World", "Items ${entities.size}")

        //Triggers
        //the order of the collisions matters here, it is recommended for the player
        //to be checked first. When jumping on top of a goomba it will set its entity as not 'active
        //and the goomba check wont't be performed preventing hurting the player
        entities.asSequence()
                .filter { it.sensors.isNotEmpty() }
                .forEach { entity ->
                    entities
                            .asSequence()
                            .filter { it != entity }
                            .forEach { checkCollisions(entity, it) }
                }

        //Tiles collisions
        for (entity in entities) {

            if (entity.velocity.y > -MAX_FALL_SPEED) {
                entity.velocity.y -= entity.gravity * gravity
            }
            if (entity.velocity.x != 0f) stepX(entity)
            if (entity.velocity.y != 0f) stepY(entity)

        }

        //AABB collisions
        entities.asSequence()
                .filter { (it.velocity.len2() > 0f) }
                .forEach { entity ->
                    entities.asSequence()
                            .filter { other -> other != entity }
                            .forEach { other ->
                                val A = Rectangle(entity.position.x + entity.velocity.x, entity.position.y + entity.velocity.y, entity.width.toFloat(), entity.height.toFloat())
                                val B = Rectangle(other.position.x + other.velocity.x, other.position.y + other.velocity.y, other.width.toFloat(), other.height.toFloat())

                                val w = (A.width + B.width) / 2f
                                val h = (A.height + B.height) / 2f
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
                            }
                }


    }

    private fun checkCollisions(entity: Entity, other: Entity) {

        val rec2 = Rectangle(other.position.x, other.position.y, other.width.toFloat(), other.height.toFloat())
        for (sensor in entity.sensors) {
            if (rec2.overlaps(Rectangle(entity.position.x + sensor.rectangle.x, entity.position.y + sensor.rectangle.y, sensor.rectangle.width, sensor.rectangle.height))) {
                sensor.f(other)
            }
        }

    }

    fun render(pm: Matrix4) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.projectionMatrix = pm

        for (entity in entities) {

            if (!entity.isVisible) continue

            shapeRenderer.setColor(1f, 0f, 0f, 1f)
            shapeRenderer.rect(entity.position.x,
                    entity.position.y,
                    entity.width.toFloat(),
                    entity.height.toFloat())
            shapeRenderer.setColor(0f, 1f, 0f, 1f)
            for (sensor in entity.sensors) {
                shapeRenderer.rect(entity.position.x + sensor.rectangle.x, entity.position.y + sensor.rectangle.y, sensor.rectangle.width, sensor.rectangle.height)
            }
        }
        shapeRenderer.end()
    }

    companion object {
        const val MAX_FALL_SPEED = 12f
        const val TILE_SIZE = 16
    }
}
