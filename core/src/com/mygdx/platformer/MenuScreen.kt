package com.mygdx.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

/**
 * Created by feresr on 17/11/17.
 */
class MenuScreen(game: Platformer) : Screen {

    private val viewport: Viewport = FitViewport(Platformer.V_WIDTH, Platformer.V_HEIGHT, OrthographicCamera())
    private val stage: Stage = Stage(viewport, game.batch)
    private val font = Label.LabelStyle(BitmapFont(), Color.WHITE)
    private val table = Table()


    override fun hide() {


    }


    override fun show() {
        table.center()
        table.setFillParent(true)

        val gameoverlabel = Label("GAME OVER", font)
        val playAgainLabel = Label("Click to Play Again", font)


        table.add(gameoverlabel).expandX
        table.row()
        table.add(playAgainLabel).expandX().padTop(10f)
        stage.addActor(table)
    }

    override fun render(delta: Float) {
        (Gdx.gl.glClearColor(0f, 0f, 0f, 1f))
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.draw()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {
        stage.dispose()
    }

}