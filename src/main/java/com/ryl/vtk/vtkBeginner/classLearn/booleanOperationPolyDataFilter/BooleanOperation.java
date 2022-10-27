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
        vtkNamedColors colors = new vtkNamedColors();
        double[] input1ActorColor = new double[3];
        colors.GetColor("Tomato", input1ActorColor);
        double[] input2ActorColor = new double[3];
        colors.GetColor("Mint", input2ActorColor);
        double[] booleanOperationActorColor = new double[3];
        colors.GetColor("Banana", booleanOperationActorColor);
        double[] rendererColor = new double[3];
        colors.GetColor("Silver", rendererColor);

        vtkActor booleanOperationActor = getBooleanOperationActor(10, 1);
        booleanOperationActor.GetProperty().SetColor(booleanOperationActorColor);
        booleanOperationActor.SetPosition(0,0,0);
        //Create the renderer, render window and interactor
        vtkRenderer renderer = new vtkRenderer();
        //renderer.AddViewProp(sphereActor);
        //renderer.AddViewProp(cylinderActor);
        renderer.AddActor(booleanOperationActor);
        //renderer.AddViewProp(booleanOperationActor);
        renderer.SetBackground(rendererColor);

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

    private static vtkActor getBooleanOperationActor(double x, int operation){
        double centerSeparation = 0.15;
        vtkSphereSource sphere = new vtkSphereSource();
        vtkCylinderSource cylinder = new vtkCylinderSource();
        //2.设置基本属性
        sphere.SetRadius(2.0);
        sphere.SetThetaResolution(40);
        sphere.SetPhiResolution(40);
        sphere.SetCenter(-centerSeparation + x, 0.0, 0.0);
        sphere.Update();
        cylinder.SetRadius(3.0);
        cylinder.SetHeight(40);
        cylinder.SetCenter(centerSeparation + x, 0.0, 0.0);
        cylinder.Update();

        vtkBooleanOperationPolyDataFilter boolFilter = new vtkBooleanOperationPolyDataFilter();
        boolFilter.SetOperation(operation);
        boolFilter.SetInputConnection(0, sphere.GetOutputPort());
        boolFilter.SetInputConnection(1, cylinder.GetOutputPort());

        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInputConnection(boolFilter.GetOutputPort());
        vtkActor actor = new vtkActor();
        actor.SetMapper(mapper);
        return actor;
    }

    private static void positionCamera(vtkRenderer renderer, double[] viewUp, double[] position){
        renderer.GetActiveCamera().SetViewUp(viewUp);
        renderer.GetActiveCamera().SetPosition(position);
        renderer.ResetCamera();
    }
}
