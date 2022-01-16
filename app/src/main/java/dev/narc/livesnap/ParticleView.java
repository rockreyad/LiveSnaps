package dev.narc.livesnap;

import android.content.Context;
import android.view.View;

public abstract class ParticleView extends View {


    public ParticleView(Context context) {
        super(context);
    }

    public abstract void setPaintColor(int color);
}
