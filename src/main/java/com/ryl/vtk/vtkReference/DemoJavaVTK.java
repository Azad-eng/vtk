package com.ryl.vtk.vtkReference;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import vtk.*;

/* ************************************************************
 * Demo applications showcasing how to use VTK with Java
 *
 * Based on SimpleVTK.java example distributed with VTK
 *
 * For more information see:
 * http://www.particleincell.com/2011/vtk-java-visualization
 *
 * Information about VTK can be found at:
 * http://vtk.org/
 *
 * ***********************************************************/

/**
 * @author : EFL-ryl
 */
public class DemoJavaVTK extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private vtkPanel renWin;
    private vtkActor cutActor;
    private vtkActor isoActor;

    private JPanel buttons;
    private JToggleButton slicesButton;
    private JToggleButton isoButton;
    private JButton exitButton;

    /* Load VTK shared librarires (.dll) on startup, print message if not found */
    static {
        //System.load("C:\\Program Files\\Java\\jdk1.8.0_211\\jre\\bin\\awt.dll");
        System.load("C:\\Program Files\\Java\\jdk1.8.0_211\\jre\\bin\\jawt.dll");
        if (!vtkNativeLibrary.LoadAllNativeLibraries()) {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
                if (!lib.IsLoaded())
                    System.out.println(lib.GetLibraryName() + " not loaded");
            }

            System.out.println("Make sure the search path is correct: ");
            System.out.println(System.getProperty("java.library.path"));
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }

    /* Constructor - generates visualization pipeline and adds actors*/
    public DemoJavaVTK() {
        super(new BorderLayout()); /* large center and small border areas*/

        double radius = 0.8;        /*sphere radius*/

        /**** 1) INPUT DATA: Sphere Implicit Function ****/
        vtkSphere sphere = new vtkSphere();
        sphere.SetRadius(radius);

        vtkSampleFunction sample = new vtkSampleFunction();
        sample.SetSampleDimensions(50, 50, 50);
        sample.SetImplicitFunction(sphere);

        /**** 2) PIPELINE 1: Isosurface Actor ****/

        /* contour filter - will generate isosurfaces from 3D data*/
        vtkContourFilter contour = new vtkContourFilter();
        contour.SetInputConnection(sample.GetOutputPort());
        contour.GenerateValues(3, 0, 1);

        /* mapper, translates polygonal representation to graphics primitives */
        vtkPolyDataMapper isoMapper = new vtkPolyDataMapper();
        isoMapper.SetInputConnection(contour.GetOutputPort());

        /*isosurface actor*/
        isoActor = new vtkActor();
        isoActor.SetMapper(isoMapper);

        /**** 3) PIPELINE 2: Cutting Plane Actor ****/

        /* define a plane in x-y plane and passing through the origin*/
        vtkPlane plane = new vtkPlane();
        plane.SetOrigin(0, 0, 0);
        plane.SetNormal(0, 0, 1);

        /* cutter, basically interpolates source data onto the plane */
        vtkCutter planeCut = new vtkCutter();
        planeCut.SetInputConnection(sample.GetOutputPort());
        planeCut.SetCutFunction(plane);
        /*this will actually create 3 planes at the subspace where the implicit
         * function evaluates to -0.7, 0, 0.7 (0 would be original plane). In
         * our case this will create three x-y planes passing through
         * z=-0.7, z=0, and z=+0.7*/
        planeCut.GenerateValues(3, -0.7, 0.7);

        /* look up table, we want to reduce number of values to get discrete bands */
        vtkLookupTable lut = new vtkLookupTable();
        lut.SetNumberOfTableValues(5);

        /* mapper, using our custom LUT */
        vtkPolyDataMapper cutMapper = new vtkPolyDataMapper();
        cutMapper.SetInputConnection(planeCut.GetOutputPort());
        cutMapper.SetLookupTable(lut);

        /* cutting plane actor, looks much better with flat shading */
        cutActor = new vtkActor();
        cutActor.SetMapper(cutMapper);
        cutActor.GetProperty().SetInterpolationToFlat();

        /**** 4) PIPELINE 3: Surface Geometry Actor ****/

        /* create polygonal representation of a sphere */
        //vtkSphereSource surf = new vtkSphereSource();
        //surf.SetRadius(radius);

        String fn1 = "stl/eye-back-left.stl";
        vtkSTLReader surf = new vtkSTLReader();
        surf.SetFileName(fn1);
        surf.Update();

        /* another mapper*/
        vtkPolyDataMapper surfMapper = new vtkPolyDataMapper();
        surfMapper.SetInputConnection(surf.GetOutputPort());

        /* surface geometry actor, turn on edges and apply flat shading*/
        vtkActor surfActor = new vtkActor();
        surfActor.SetMapper(surfMapper);
        //EdgeVisibilityOn/Off 是否显示边线
        surfActor.GetProperty().EdgeVisibilityOn();
        surfActor.GetProperty().SetEdgeColor(0.2, 0.2, 0.2);
        //关闭柔化效果
        surfActor.GetProperty().SetInterpolationToFlat();



        /**** 5) RENDER WINDOW ****/

        /* vtkPanel - this is the interface between Java and VTK */
        renWin = new vtkPanel();

        /* add the surface geometry plus the isosurface */
        renWin.GetRenderer().AddActor(surfActor);
        renWin.GetRenderer().AddActor(isoActor);

        /* the default zoom is whacky, zoom out to see the whole domain */
        renWin.GetRenderer().GetActiveCamera().Dolly(0.2);
        renWin.GetRenderer().SetBackground(1, 1, 1);

        /**** 6) CREATE PANEL FOR BUTTONS ****/
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 0));

        /* isosurface button, clicked by default */
        isoButton = new JToggleButton("Isosurfaces", true);
        isoButton.addActionListener(this);

        /* cutting planes button */
        slicesButton = new JToggleButton("Slices");
        slicesButton.addActionListener(this);

        /* exit button */
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        /* add buttons to the panel */
        buttons.add(isoButton);
        buttons.add(slicesButton);
        buttons.add(exitButton);

        /**** 7) POPULATE MAIN PANEL ****/
        add(renWin, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    /* ActionListener that responds to button clicks
     * Toggling iso/slices buttons results in addition or removal
     * of the corresponding actor */
    public void actionPerformed(ActionEvent e) {
        /*cutting planes button, add or remove cutActor */
        if (e.getSource().equals(slicesButton)) {
            if (slicesButton.isSelected())
                renWin.GetRenderer().AddActor(cutActor);
            else
                renWin.GetRenderer().RemoveActor(cutActor);

            renWin.Render();
        }
        /*isosurface button, add or remove isoActor */
        else if (e.getSource().equals(isoButton)) {
            if (isoButton.isSelected())
                renWin.GetRenderer().AddActor(isoActor);
            else
                renWin.GetRenderer().RemoveActor(isoActor);
            renWin.Render();
        }
        /*exit button, end application */
        else if (e.getSource().equals(exitButton)) {
            System.exit(0);
        }
    }

    /* main, creates a new JFrame and populates it with the DemoJavaVTK panel */
    public static void main(String s[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Java and VTK Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(new DemoJavaVTK(), BorderLayout.CENTER);
                frame.setSize(400, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static vtkPolyData readPolyData(String fileName){
        vtkPolyData polyData;
        if (fileName.endsWith(".stl")){
            vtkSTLReader vtkReader = new vtkSTLReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else if (fileName.endsWith(".obj")) {
            vtkOBJReader vtkReader = new vtkOBJReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else if (fileName.endsWith(".ply")) {
            vtkPLYReader vtkReader = new vtkPLYReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else if (fileName.endsWith(".vtp")) {
            vtkXMLPolyDataReader vtkReader = new vtkXMLPolyDataReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else if (fileName.endsWith(".vtk")) {
            vtkPolyDataReader vtkReader = new vtkPolyDataReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else if (fileName.endsWith(".g")) {
            vtkBYUReader vtkReader = new vtkBYUReader();
            vtkReader.SetFileName(fileName);
            vtkReader.Update();
            polyData = vtkReader.GetOutput();
        } else {
            polyData = null;
        }

        return polyData;
    }

    public String loadModel(String filepath){
        boolean success;
        File stlFile = new File(filepath);
        success = loadBinaryStl(stlFile);
        if (!success) {
            loadAsciiStl(stlFile);
        }
        return stlFile.getName();
    }

    /**
     * 尝试读取二进制格式的stl文件
     *
     * @param stlFile 文件路径
     * @return 读取是否成功或者是否是一个二进制格式的stl文件
     */
    private boolean loadBinaryStl(File stlFile){
        if (stlFile == null) {
            return false;
        }
        long fileLength = stlFile.length();
        if ((fileLength - 84) % 50 != 0) {
            return false;
        }

        try (FileInputStream inputStream = new FileInputStream(stlFile)) {
            byte[] bytes = new byte[80];
            inputStream.read(bytes);
            bytes = new byte[4];
            inputStream.read(bytes);
            int num = ByteArrayToInt_LE(bytes, 0);
            if (num != (fileLength - 84) / 50) {
                System.out.println("二进制stl文件中的三角面片数量与文件长度不符，加载失败");
                return false;
            }
        } catch (IOException e) {
            System.out.println("stl文件读取异常" + e);
            return false;
        } catch (OutOfMemoryError error) {
            System.out.println("内存不足" + error);
        }
        return true;
    }

    private int ByteArrayToInt_LE(byte[] arr, int offset) {
        if (arr.length < 4 + offset) {
            return -1;
        }
        return arr[offset] & 0xff | (arr[offset + 1] & 0xff) << 8 | (arr[offset + 2] & 0xff) << 16 | arr[offset + 3] << 24;
    }

    /**
     * 尝试读取ASCII格式的stl文件
     *
     * @return 读取是否成功或者是否是一个标准的ASCII格式的stl文件
     */
    private boolean loadAsciiStl(File stlFile) {
        if (stlFile == null) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(stlFile))) {
            int readLine = 0;
            //根据行数大概计算出三角面片数量，标准格式是每7行定义一个三角面片，用于初始化List的容量，避免读取的时候多次扩容，提升效率。
            //+1多给一个容量避免计算误差导致有一次扩容
            while (reader.readLine() != null) {
                readLine++;
            }
            return true;
        } catch (IOException e) {
            return false;
        } catch (OutOfMemoryError ignored) {
        }
        return true;
    }
}