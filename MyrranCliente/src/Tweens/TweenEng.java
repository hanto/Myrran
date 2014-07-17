package Tweens;// Created by Hanto on 15/07/2014.

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.OrthographicCamera;

public enum TweenEng
{
    GET;

    private static TweenManager tweenManager= new TweenManager();
    public static TweenManager getTweenManager()   { return tweenManager; }

    //Aqui se registran todos los Accessors del Tween-Engine:
    private TweenEng()
    {   Tween.registerAccessor(OrthographicCamera.class, new CamaraTween()); }
}