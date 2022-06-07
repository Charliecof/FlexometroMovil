package com.example.flexometromovil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kotlin.collections.AbstractMutableMap;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private com.google.ar.sceneform.rendering.Color arColor = new com.google.ar.sceneform.rendering.Color(Color.RED);
    private ArFragment arFragment;
    private TextView distanceModeTextView = null;

    private ArrayList placedAnchors = new ArrayList<Anchor>();
    private ArrayList placedAnchorNodes = new ArrayList<AnchorNode>();
    private List fromGroundNodes = new ArrayList<List<Node>>();
    // private AbstractMutableMap midAnchors = new AbstractMutableMap<String,Anchor>();
    private HashMap midAnchors = new HashMap<String, Anchor>();
    private HashMap midAnchorNodes = new HashMap<String, AnchorNode>();

    private ViewRenderable distanceCardViewRenderable = null;
    private ModelRenderable cubeRenderable = null;

    // UP ONE
    private Renderable arrow1UpRenderable;
    private LinearLayout arrow1UpLinearLayout;
    private ImageView arrow1UpView;
    // DOWN ONE
    private Renderable arrow1DownRenderable;
    private LinearLayout arrow1DownLinearLayout;
    private ImageView arrow1DownView;
    // UP FIVE
    private Renderable arrow5UpRenderable;
    private LinearLayout arrow5UpLinearLayout;
    private ImageView arrow5UpView;
    // DOWN FIVE
    private Renderable arrow5DownRenderable;
    private LinearLayout arrow5DownLinearLayout;
    private ImageView arrow5DownView;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        distanceModeTextView = findViewById(R.id.distance_view);

        maybeEnableArButton();

        initArrowView();
        initRenderable();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            // aqui va el metodo para medir
            //distanceDistanceFromGround(hitResult);
            distance2Points(hitResult);
        });


    }

    private void placeAnchor(HitResult hitResult, Renderable renderable) {
        Anchor anchor = hitResult.createAnchor();
        placedAnchors.add(anchor);

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setSmoothed(true);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        placedAnchorNodes.add(anchorNode);

        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.getRotationController().setEnabled(false);
        node.getScaleController().setEnabled(false);
        node.getTranslationController().setEnabled(true);
        node.setRenderable(renderable);
        node.setParent(anchorNode);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    private void distanceDistanceFromGround(HitResult hitResult) {
        Anchor anchor = hitResult.createAnchor();
        placedAnchors.add(anchor);

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setSmoothed(true);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        placedAnchorNodes.add(anchorNode);

        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);

        Node node = new Node();
        node.setParent(transformableNode);
        node.setWorldPosition(new Vector3(anchorNode.getWorldPosition().x, anchorNode.getWorldPosition().y, anchorNode.getWorldPosition().z));
        node.setRenderable(distanceCardViewRenderable);

        // UP ONE
        Node arrow1UpNode = new Node();
        arrow1UpNode.setParent(node);
        arrow1UpNode.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 0.1f, node.getWorldPosition().z));
        arrow1UpNode.setRenderable(arrow1UpRenderable);
        arrow1UpNode.setOnTapListener((hitTestResult, motionEvent) -> {
            node.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 0.01f, node.getWorldPosition().z));
        });

        // DOWN ONE
        Node arrow1DownNode = new Node();
        arrow1DownNode.setParent(node);
        arrow1DownNode.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y - 0.08f, node.getWorldPosition().z));
        arrow1DownNode.setRenderable(arrow1DownRenderable);
        arrow1DownNode.setOnTapListener((hitTestResult, motionEvent) -> {
            node.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y - 0.01f, node.getWorldPosition().z));
        });

        // UP FIVE
        Node arrow5UpNode = new Node();
        arrow5UpNode.setParent(node);
        arrow5UpNode.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 0.18f, node.getWorldPosition().z));
        arrow5UpNode.setRenderable(arrow1UpRenderable);
        arrow5UpNode.setOnTapListener((hitTestResult, motionEvent) -> {
            node.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 0.1f, node.getWorldPosition().z));
        });

        // DOWN FIVE
        Node arrow5DownNode = new Node();
        arrow5DownNode.setParent(node);
        arrow5DownNode.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y - 0.167f, node.getWorldPosition().z));
        arrow5DownNode.setRenderable(arrow1DownRenderable);
        arrow5DownNode.setOnTapListener((hitTestResult, motionEvent) -> {
            node.setWorldPosition(new Vector3(node.getWorldPosition().x, node.getWorldPosition().y - 0.1f, node.getWorldPosition().z));
        });

        fromGroundNodes.add(Arrays.asList(node, arrow1UpNode, arrow1DownNode, arrow5UpNode, arrow5DownNode));

        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }

    private void distance2Points(HitResult hitResult) {

        switch (placedAnchorNodes.size()) {
            case 0:
                placeAnchor(hitResult, cubeRenderable);
                break;
            case 1:
                placeAnchor(hitResult, cubeRenderable);
                float position[] = new float[3];
                AnchorNode anchorNode = (AnchorNode) placedAnchorNodes.get(0);
                position[0] = anchorNode.getWorldPosition().x / 2;
                position[1] = anchorNode.getWorldPosition().y / 2;
                position[2] = anchorNode.getWorldPosition().z / 2;
                float quaternion[] = new float[4];
                for (int i = 0; i < quaternion.length; i++)
                    quaternion[i] = 0.0f;
                Pose pose = new Pose(position, quaternion);
                placeMidAnchor(pose, distanceCardViewRenderable);
                break;
            default:
                clearAllAnchors();
                placeAnchor(hitResult, cubeRenderable);
        }
    }

    private void measureDistance2Points() {
        if (placedAnchorNodes.size() == 2) {
            AnchorNode anchorNode1 = (AnchorNode) placedAnchorNodes.get(0);
            AnchorNode anchorNode2 = (AnchorNode) placedAnchorNodes.get(1);
            float distanceMeter = calculateDistance(anchorNode1.getWorldPosition(), anchorNode2.getWorldPosition());
            measureDistance2Points(distanceMeter);
        }
    }

    private void measureDistance2Points(float distanceMeter) {
        String distanceText = distanceTextCm(distanceMeter);
        TextView textView = (TextView) distanceCardViewRenderable.getView().findViewById(R.id.distanceCard);
        textView.setText(distanceText);
        Log.d("Distance", "distance: " + distanceText);
    }

    private float calculateDistance(float x, float y, float z) {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    private float calculateDistance(Pose pose, Pose pose2) {
        return calculateDistance(pose.tx() - pose2.tx(), pose.ty() - pose2.ty(), pose.ty() - pose2.ty());
    }

    private float calculateDistance(Vector3 object1, Vector3 object2) {
        return calculateDistance(object1.x - object2.x, object1.y - object2.y, object1.z - object2.z);
    }

    private float calculateDistance(Vector3 object1, Pose object2) {
        return calculateDistance(object1.x - object2.tx(), object1.y - object2.ty(), object1.z - object2.tz());
    }

    private void placeMidAnchor(Pose pose, Renderable renderable) {
        String midKey = "0_1";
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
        midAnchors.put(midKey, anchor);

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setSmoothed(true);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        midAnchorNodes.put(midKey, anchorNode);

        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.getRotationController().setEnabled(false);
        node.getScaleController().setEnabled(false);
        node.getTranslationController().setEnabled(true);
        node.setRenderable(renderable);
        node.setParent(anchorNode);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            Toast.makeText(this, "Es compatible", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No es compatible", Toast.LENGTH_LONG).show();
        }
    }

    private void measureDistanceFromGround() {
        if (fromGroundNodes.size() == 0) return;
        int aux2 = 0;
        for (Iterator<ArrayList> nodeArrayList = fromGroundNodes.iterator(); nodeArrayList.hasNext(); ) {
            List<Node> aux = nodeArrayList.next();
            TextView textView = distanceCardViewRenderable.getView().findViewById(R.id.distanceCard);
            Float distance = changeUnit(aux.get(aux2).getWorldPosition().y + 1.0f, "cm");
            textView.setText(distance + " cm");
            Log.d("Debugg", String.valueOf(aux));
            aux2 += 1;
        }

        Toast.makeText(this, "measuring", Toast.LENGTH_SHORT).show();
    }

    private void initArrowView() {
        // UP ONE
        arrow1UpLinearLayout = new LinearLayout(this);
        arrow1UpLinearLayout.setOrientation(LinearLayout.VERTICAL);
        arrow1UpLinearLayout.setGravity(Gravity.CENTER);
        arrow1UpView = new ImageView(this);
        arrow1UpView.setImageResource(R.drawable.upone);
        arrow1UpLinearLayout.addView(arrow1UpView, 45, 45);

        // DOWN ONE
        arrow1DownLinearLayout = new LinearLayout(this);
        arrow1DownLinearLayout.setOrientation(LinearLayout.VERTICAL);
        arrow1DownLinearLayout.setGravity(Gravity.CENTER);
        arrow1DownView = new ImageView(this);
        arrow1DownView.setImageResource(R.drawable.downone);
        arrow1DownLinearLayout.addView(arrow1DownView, 45, 45);

        // UP FIVE
        arrow5UpLinearLayout = new LinearLayout(this);
        arrow5UpLinearLayout.setOrientation(LinearLayout.VERTICAL);
        arrow5UpLinearLayout.setGravity(Gravity.CENTER);
        arrow5UpView = new ImageView(this);
        arrow5UpView.setImageResource(R.drawable.upfive);
        arrow5UpLinearLayout.addView(arrow5UpView, 45, 45);

        // DOWN ONE
        arrow5DownLinearLayout = new LinearLayout(this);
        arrow5DownLinearLayout.setOrientation(LinearLayout.VERTICAL);
        arrow5DownLinearLayout.setGravity(Gravity.CENTER);
        arrow5DownView = new ImageView(this);
        arrow5DownView.setImageResource(R.drawable.downfive);
        arrow5DownLinearLayout.addView(arrow5DownView, 45, 45);
    }

    private void initRenderable() {
        MaterialFactory
                .makeTransparentWithColor(this, new com.google.ar.sceneform.rendering.Color(Color.BLUE))
                .thenAccept((Material material) -> {
                    cubeRenderable = ShapeFactory.makeSphere(0.02f, Vector3.zero(), material);
                    cubeRenderable.setShadowCaster(false);
                    cubeRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    Toast.makeText(this, "Error Rendering point ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

        ViewRenderable
                .builder()
                .setView(this, R.layout.distance_layout)
                .build()
                .thenAccept((it) -> {
                    distanceCardViewRenderable = it;
                    distanceCardViewRenderable.setShadowCaster(false);
                    distanceCardViewRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

        ViewRenderable
                .builder()
                .setView(this, arrow1UpLinearLayout).build()
                .thenAccept((it) -> {
                    arrow1UpRenderable = it;
                    arrow1UpRenderable.setShadowCaster(false);
                    arrow1UpRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

        ViewRenderable
                .builder()
                .setView(this, arrow1DownLinearLayout)
                .build()
                .thenAccept((it) -> {
                    arrow1DownRenderable = it;
                    arrow1DownRenderable.setShadowCaster(false);
                    arrow1DownRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

        ViewRenderable
                .builder()
                .setView(this, arrow5UpLinearLayout)
                .build()
                .thenAccept((it) -> {
                    arrow5UpRenderable = it;
                    arrow5UpRenderable.setShadowCaster(false);
                    arrow5UpRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });

        ViewRenderable
                .builder()
                .setView(this, arrow5DownLinearLayout)
                .build()
                .thenAccept((it) -> {
                    arrow5DownRenderable = it;
                    arrow5DownRenderable.setShadowCaster(false);
                    arrow5DownRenderable.setShadowReceiver(false);
                })
                .exceptionally((ex) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(ex.getMessage()).setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });
    }

    private void clearAllAnchors() {
        placedAnchors.clear();
        if (placedAnchorNodes.size() <= 0 || midAnchorNodes.size() <= 0) return;
        Log.d("Clear anchors", "Clearing"+placedAnchorNodes.size());
        for (int i = 0; i < placedAnchorNodes.size(); i++) {
            AnchorNode anchorNode = (AnchorNode) placedAnchorNodes.get(i);
            arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.setEnabled(false);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
        }
        placedAnchorNodes.clear();
        midAnchors.clear();
        for (int i = 0; i < midAnchorNodes.size(); i++) {
            AnchorNode anchorNode = (AnchorNode) midAnchorNodes.get(i);
            arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.setEnabled(false);
            anchorNode.getAnchor().detach();
            anchorNode.setParent(null);
        }
        midAnchorNodes.clear();

        fromGroundNodes.clear();
    }

    private Float changeUnit(Float distancia, String unidad) {
        switch (unidad) {
            case "cm":
                distancia *= 100;
                break;
            case "mm":
                distancia *= 1000;
                break;
            default:
                distancia = distancia;
        }
        return distancia;
    }

    private String distanceTextCm(float distanceMeter) {
        float distance = changeUnit(distanceMeter, "cm");
        String distanceCM = "" + distance;
        return distanceCM + " cm";
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        //measureDistanceFromGround();
        measureDistance2Points();
    }
}