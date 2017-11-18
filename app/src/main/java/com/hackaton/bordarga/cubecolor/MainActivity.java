package com.hackaton.bordarga.cubecolor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Wrapper activity demonstrating the use of the new
 * {@link SensorEvent#values rotation vector sensor}
 * ({@link Sensor#TYPE_ROTATION_VECTOR TYPE_ROTATION_VECTOR}).
 *
 * @see Sensor
 * @see SensorEvent
 * @see SensorManager
 *
 */
public class MainActivity extends Activity {
    private GLSurfaceView mGLSurfaceView;
    private SensorManager mSensorManager;
    private MyRenderer mRenderer;

    private RadioGroup rgroup;
    private RadioButton[] rbuttons = new RadioButton[9];
    private SeekBar[] seekBars = new SeekBar[4];
    private TextView[] textViews = new TextView[4];
    private TextView[] textViewsTitle = new TextView[4];
    private Button hideBt;

    private int lastRadioClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgroup = (RadioGroup)findViewById(R.id.radioGroup);
        rbuttons[0] = (RadioButton)findViewById(R.id.radioButton1);
        rbuttons[1] = (RadioButton)findViewById(R.id.radioButton2);
        rbuttons[2] = (RadioButton)findViewById(R.id.radioButton3);
        rbuttons[3] = (RadioButton)findViewById(R.id.radioButton4);
        rbuttons[4] = (RadioButton)findViewById(R.id.radioButton5);
        rbuttons[5] = (RadioButton)findViewById(R.id.radioButton6);
        rbuttons[6] = (RadioButton)findViewById(R.id.radioButton7);
        rbuttons[7] = (RadioButton)findViewById(R.id.radioButton8);
        rbuttons[8] = (RadioButton)findViewById(R.id.radioButton9);

        seekBars[0] = (SeekBar)findViewById(R.id.seekBar1);
        seekBars[1] = (SeekBar)findViewById(R.id.seekBar2);
        seekBars[2] = (SeekBar)findViewById(R.id.seekBar3);
        seekBars[3] = (SeekBar)findViewById(R.id.seekBar4);

        textViews[0] = (TextView)findViewById(R.id.textViewR);
        textViews[1] = (TextView)findViewById(R.id.textViewG);
        textViews[2] = (TextView)findViewById(R.id.textViewB);
        textViews[3] = (TextView)findViewById(R.id.textViewA);

        textViewsTitle[0] = (TextView)findViewById(R.id.textView);
        textViewsTitle[1] = (TextView)findViewById(R.id.textView2);
        textViewsTitle[2] = (TextView)findViewById(R.id.textView3);
        textViewsTitle[3] = (TextView)findViewById(R.id.textView4);

        setRadioButtons();
        setSeekBars();


        hideBt = (Button)findViewById(R.id.hideBt);
        hideBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int set = rgroup.getVisibility() == View.VISIBLE? View.INVISIBLE : View.VISIBLE;

                rgroup.setVisibility(set);
                for(RadioButton r : rbuttons)
                    r.setVisibility(set);
                for(SeekBar s : seekBars)
                    s.setVisibility(set);
                for(TextView t : textViews)
                    t.setVisibility(set);
                for(TextView t : textViewsTitle)
                    t.setVisibility(set);

                findViewById(R.id.textView5).setVisibility(set);

                hideBt.setText(set == View.VISIBLE? "Hide" : "Show");
            }
        });

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Create our Preview view and set it as the content of our
        // Activity
        mRenderer = new MyRenderer();

        mGLSurfaceView = (GLSurfaceView)findViewById(R.id.gl_surface);

        //mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        // The renderer will be implemented in a separate class, GLView, which I'll show next.
        mGLSurfaceView.setZOrderOnTop(true);

        mGLSurfaceView.setRenderer(mRenderer);
        //setContentView(mGLSurfaceView);
    }
    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mRenderer.start();
        mGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mRenderer.stop();
        mGLSurfaceView.onPause();
    }

    private void setRadioButtons(){
        for(int i = 0; i < 8; ++i){
            final int index = i;
            rbuttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastRadioClicked = index;
                    int[] r = mRenderer.getCube().getColor(index);
                    for(int j = 0; j < r.length; ++j)
                        seekBars[j].setProgress(r[j]);
                }
            });
        }

        rbuttons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastRadioClicked = 8;
                for(int i = 0; i < 4; ++i)
                    seekBars[i].setProgress((int) (mRenderer.backgroundColor[i] * 255));
            }
        });

    }
    private void setSeekBars(){
        for(int i = 0; i < seekBars.length; ++i){
            final int index = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViews[index].setText("" +progress);

                    if(lastRadioClicked != -1){
                        if(lastRadioClicked < 8){
                            ;                           mRenderer.getCube().setPointChannel(lastRadioClicked, progress, index);
                        }else{
                            mRenderer.setBackgroundChannel(progress, index);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

    }

    class MyRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        private Cube mCube;
        private Sensor mRotationVectorSensor;
        private final float[] mRotationMatrix = new float[16];

        long startTime = System.nanoTime();
        int frames = 0;

        private boolean changeBack = false;

        public float[] backgroundColor = new float[4];

        public Cube getCube(){
            return mCube;
        }

        public void setBackgroundChannel(int value, int channel){
            backgroundColor[channel] = value / 255.0f;
            changeBack = true;
        }



        public MyRenderer() {
            // find the rotation-vector sensor
            mRotationVectorSensor = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR);
            mCube = new Cube();
            // initialize the rotation matrix to identity
            mRotationMatrix[ 0] = 1;
            mRotationMatrix[ 4] = 1;
            mRotationMatrix[ 8] = 1;
            mRotationMatrix[12] = 1;

            backgroundColor = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        }
        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 10 ms updates.
            mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
        }
        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }
        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // convert the rotation-vector to a 4x4 matrix. the matrix
                // is interpreted by Open GL as the inverse of the
                // rotation-vector, which is what we want.
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix , event.values);
            }
        }
        public void onDrawFrame(GL10 gl) {
            frames++;
            if(System.nanoTime() - startTime >= 1000000000) {
                Log.v("FPSCounter", "fps: " + frames);
                frames = 0;
                startTime = System.nanoTime();
            }

            if(changeBack) {
                gl.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
                changeBack = false;
            }
            // clear screen
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            // set-up modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -3.0f);
            gl.glMultMatrixf(mRotationMatrix, 0);
            // draw our object
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            mCube.draw(gl);
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // set view-port
            gl.glViewport(0, 0, width, height);
            // set projection matrix
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // dither is enabled by default, we don't need it
            gl.glDisable(GL10.GL_DITHER);
            // clear screen in white
            gl.glClearColor(backgroundColor[0], backgroundColor[1],backgroundColor[2],backgroundColor[3]);
        }
        class Cube {
            // initialize our cube
            private FloatBuffer mVertexBuffer;
            private FloatBuffer mColorBuffer;
            private ByteBuffer  mIndexBuffer;

            private float[] colors;

            public Cube() {
                final float vertices[] = {
                        -1, -1, -1,		 1, -1, -1,
                        1,  1, -1,	    -1,  1, -1,
                        -1, -1,  1,      1, -1,  1,
                        1,  1,  1,     -1,  1,  1,
                };
                colors = new float[]{
                        0,  0,  0,  1,  1,  0,  0,  1,
                        1,  1,  0,  1,  0,  1,  0,  1,
                        0,  0,  1,  1,  1,  0,  1,  1,
                        1,  1,  1,  1,  0,  1,  1,  1,
                };
                final byte indices[] = {
                        0, 4, 5,    0, 5, 1,
                        1, 5, 6,    1, 6, 2,
                        2, 6, 7,    2, 7, 3,
                        3, 7, 4,    3, 4, 0,
                        4, 7, 6,    4, 6, 5,
                        3, 0, 1,    3, 1, 2
                };
                ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
                vbb.order(ByteOrder.nativeOrder());
                mVertexBuffer = vbb.asFloatBuffer();
                mVertexBuffer.put(vertices);
                mVertexBuffer.position(0);
                ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
                cbb.order(ByteOrder.nativeOrder());
                mColorBuffer = cbb.asFloatBuffer();
                mColorBuffer.put(colors);
                mColorBuffer.position(0);
                mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
                mIndexBuffer.put(indices);
                mIndexBuffer.position(0);
            }
            public void draw(GL10 gl) {
                gl.glEnable(GL10.GL_CULL_FACE);
                gl.glFrontFace(GL10.GL_CW);
                gl.glShadeModel(GL10.GL_SMOOTH);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
                gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
                gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
            }

            public int[] getColor(int point){
                int[] r = new int[4];

                for(int i = 0; i < 4; ++i)
                    r[i] = (int) (colors[point * 4 + i] * 255);

                return r;
            }

            public void setPointChannel(int point, int progress, int channel){
                colors[point * 4 + channel]  = progress / 255.0f;

                ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
                cbb.order(ByteOrder.nativeOrder());
                mColorBuffer = cbb.asFloatBuffer();
                mColorBuffer.put(colors);
                mColorBuffer.position(0);
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }



    }
}