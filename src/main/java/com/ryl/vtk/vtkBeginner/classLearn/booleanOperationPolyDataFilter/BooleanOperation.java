package com.ryl.vtk.vtkBeginner.classLearn.booleanOperationPolyDataFilter;

import vtk.*;

/**
 * @author EFL-ryl
 */
public class BooleanOperation {
    static {
        System.load("C:\\Program Files\\Java\\jdk1.8.0_211\\jre\\bin\\awt.dll");
        System.load("C:\\Program Files\\Java\\jdk1.8.0_211\\jre\\bin\\jawt.dll");
        if (!vtkNativeLibrary.LoadAllNativeLibraries()){
            for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
                if (!lib.IsLoaded()){
                    System.out.println(lib.GetLibraryName() + " not loaded");
                }
            }
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }
    public static void main(String[] args) {
        //CREATE A SPHERE AND CYLINDER
        //创建一个vtkConeSource的实例sphere
        vtkSphereSource sphere = new vtkSphereSource();
        //设置基本属性
        sphere.SetRadius(5.0);
        sphere.SetThetaResolution(10);
        sphere.SetPhiResolution(10);
        sphere.Update();
        vtkAlgorithmOutput input1 = sphere.GetOutputPort();
        //创建一个vtkConeSource的实例cylinder
        vtkCylinderSource cylinder = new vtkCylinderSource();
        cylinder.SetRadius(2.0);
        cylinder.SetHeight(20);
        cylinder.SetResolution(10);
        cylinder.Update();
        //设置基本属性
        vtkAlgorithmOutput input2 = cylinder.GetOutputPort();

        //CREATE COLORS
        //1.颜色属性
        vtkNamedColors colors = new vtkNamedColors();
        //2.actor颜色
        double[] sphereActorColor = new double[4];
        colors.GetColor("Tomato", sphereActorColor);
        double[] cylinderActorColor = new double[4];
        colors.GetColor("Mint", cylinderActorColor);
        //3.背景颜色
        double[] bgColor = new double[4];
        colors.GetColor("Silver", bgColor);

        //CREATE MAPPERS AND ACTORS
        //创建vtkPolyDataMapper并且映射sphere source得到一个相关的mapper（映射器）
        vtkPolyDataMapper sphereMapper = new vtkPolyDataMapper();
        sphereMapper.SetInputConnection(input1);
        sphereMapper.ScalarVisibilityOff();
        //创建一个sphereActor并且分配mapper
        vtkActor sphereActor = new vtkActor();
        sphereActor.SetMapper(sphereMapper);
        //设置sphereActor颜色属性
        sphereActor.GetProperty().SetColor(sphereActorColor);

        //创建vtkPolyDataMapper并且映射cylinder source得到一个相关的mapper（映射器）
        vtkPolyDataMapper cylinderMapper = new vtkPolyDataMapper();
        cylinderMapper.SetInputConnection(input2);
        cylinderMapper.ScalarVisibilityOff();
        //创建一个cylinderActor并且分配mapper
        vtkActor cylinderActor = new vtkActor();
        cylinderActor.SetMapper(cylinderMapper);
        //设置cylinderActor颜色属性
        cylinderActor.GetProperty().SetColor(cylinderActorColor);
        cylinderActor.SetPosition(0,5,0);

        //START BOOLEAN OPERATION
        vtkBooleanOperationPolyDataFilter booleanOperation = new vtkBooleanOperationPolyDataFilter();
        //booleanOperation.SetOperationToUnion();
        booleanOperation.SetOperationToIntersection();
        //booleanOperation.SetOperationToDifference();
        booleanOperation.SetInputConnection(0, input1);
        booleanOperation.SetInputConnection(1, input2);
        //创建vtkPolyDataMapper并且映射booleanOperation source得到一个相关的mapper（映射器）
        vtkPolyDataMapper booleanOperationMapper = new vtkPolyDataMapper();
        booleanOperationMapper.SetInputConnection(booleanOperation.GetOutputPort());
        booleanOperationMapper.ScalarVisibilityOff();
        //创建一个booleanOperationActor并且分配mapper
        vtkActor booleanOperationActor  = new vtkActor();
        booleanOperationActor.SetMapper(booleanOperationMapper);
        //设置booleanOperationActor颜色属性
        double[] booleanOperationActorColor = new double[3];
        colors.GetColor("Banana", booleanOperationActorColor);
        booleanOperationActor.GetProperty().SetColor(booleanOperationActorColor);
        booleanOperationActor.SetPosition(0,0,0);

        //Create the renderer, render window and interactor
        vtkRenderer renderer = new vtkRenderer();
        //renderer.AddViewProp(sphereActor);
        //renderer.AddViewProp(cylinderActor);
        renderer.AddViewProp(booleanOperationActor);
        renderer.SetBackground(bgColor);

        vtkRenderWindow renderWindow = new vtkRenderWindow();
        renderWindow.AddRenderer(renderer);
        renderWindow.SetSize(400, 360);

        double[] viewUp = {0.0, 0.0, 1.0};
        double[] position = {0.0, -1.0, 10.0};
        positionCamera(renderer, viewUp, position);
        renderer.GetActiveCamera().Dolly(1.4);
        renderer.ResetCameraClippingRange();

        vtkRenderWindowInteractor renWinInteractor = new vtkRenderWindowInteractor();
        renWinInteractor.SetRenderWindow(renderWindow);
        renderWindow.Render();
        vtkInteractorStyleTrackballCamera viewStyle = new vtkInteractorStyleTrackballCamera();
        renWinInteractor.SetInteractorStyle(viewStyle);
        renWinInteractor.Initialize();
        renWinInteractor.Start();
    }

    private static void positionCamera(vtkRenderer renderer, double[] viewUp, double[] position){
        renderer.GetActiveCamera().SetViewUp(viewUp);
        renderer.GetActiveCamera().SetPosition(position);
        renderer.ResetCamera();
    }
}
