package graph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;

import javax.swing.*;
import java.awt.*;


class visual extends JFrame {

    private JGraphXAdapter jGraphXAdapter;

    public visual(Graph gr) {
        jGraphXAdapter = new JGraphXAdapter(gr);
        setMinimumSize(new Dimension(700, 500));
        mxGraphComponent component = new mxGraphComponent(jGraphXAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        mxCircleLayout layout = new mxCircleLayout(jGraphXAdapter);

        // center the circle
        int radius = 100;
        layout.setX0((new Dimension(700, 500).width / 2.0) - radius);
        layout.setY0((new Dimension(700, 500).height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jGraphXAdapter.getDefaultParent());
    }
}
