package org.voimala.myrts.gameplay;

/* Allows the player to move the camera while holding right mouse button. */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraManagement {
    private boolean isMovingCamera = false;
    private boolean isStartPointSet = false;
    private int startX = -1;
    private int startY = -1;
    private OrthographicCamera worldCamera = null;

    public CameraManagement(OrthographicCamera worldCamera) {
        this.worldCamera = worldCamera;
    }

    public void update() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            setMovingCamera(true);

            if (!isStartPointSet()) {
                setStartX(Gdx.input.getX());
                setStartY(Gdx.input.getY());
                setStartPointSet(true);
            }
        } else {
            setMovingCamera(false);
        }

        if (isMovingCamera()) {
            worldCamera.translate(Gdx.input.getX() - getStartX(),
                    getStartY() - Gdx.input.getY());
            worldCamera.update();
        }
    }

    public boolean isStartPointSet() {
        return isStartPointSet;
    }

    public void setStartPointSet(boolean isStartPointSet) {
        this.isStartPointSet = isStartPointSet;
    }

    public boolean isMovingCamera() {
        return isMovingCamera;
    }

    /** If argument is false, resets starting point. */
    public void setMovingCamera(boolean isMovingCamera) {
        this.isMovingCamera = isMovingCamera;

        if (isMovingCamera == false) {
            startX = -1;
            startY = -1;
            isStartPointSet = false;
        }
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }
}
