package com.ryl.vtk.vtkBeginner.classLearn.booleanOperationPolyDataFilter;

import vtk.*;

/**
 * @author EFL-ryl
 */
public class BooleanOperation {
    static {
        System.load("D:\\software\\jdk1.8\\jre\\bin\\awt.dll");
        System.load("D:\\software\\jdk1.8\\jre\\bin\\jawt.dll");
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
        //1.创建一个vtkConeSource的实例
        vtkSphereSource sphere = new vtkSphereSource();
        vtkCylinderSource cylinder = new vtkCylinderSource();
        //2.设置基本属性
        sphere.SetRadius(2.0);
        sphere.SetThetaResolution(40);
        sphere.SetPhiResolution(40);
        sphere.Update();
        cylinder.SetRadius(3.0);
        cylinder.SetHeight(40);
        cylinder.Update();

        //CREATE COLORS
        //1.颜色属性
        vtkNamedColors coneNamedColors = new vtkNamedColors();
        //2.actor颜色
        double[] actorColor = new double[4];
        coneNamedColors.GetColor("MediumVioletRed", actorColor);
        //3.背景颜色
        double[] bgColor = new double[4];
        coneNamedColors.GetColor("Wheat", bgColor);

        //CREATE MAPPERS AND ACTORS
        //1.创建vtkPolyDataMapper并且映射cone source得到一个相关的mapper（映射器）
        vtkPolyDataMapper sphereMapper = new vtkPolyDataMapper();
        vtkPolyDataMapper cylinderMapper = new vtkPolyDataMapper();
        sphereMapper.SetInputConnection(sphere.GetOutputPort());
        sphereMapper.ScalarVisibilityOff();
        cylinderMapper.SetInputConnection(cylinder.GetOutputPort());
        cylinderMapper.ScalarVisibilityOff();
        //2.创建一个actor并且分配mapper
        vtkActor sphereActor = new vtkActor();
        vtkActor cylinderActor = new vtkActor();
        sphereActor.SetMapper(sphereMapper);
        cylinderActor.SetMapper(cylinderMapper);
        //3.设置actor颜色属性
        sphereActor.GetProperty().SetColor(actorColor);

        //Create the renderer, render window and interactor
        //1.创建一个render（渲染器）并且添加actors
        vtkRenderer ren = new vtkRenderer();
        ren.AddActor(sphereActor);
        ren.AddActor(cylinderActor);
        //2.创建一个render window（渲染窗口） 并添加render
        vtkRenderWindow renWin = new vtkRenderWindow();
        renWin.AddRenderer(ren);
        //3.创建一个window interactor（窗口交互器）并添加window
        vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);

        //Visualise the cone
        //1.设置渲染器背景色
        ren.SetBackground(0.3,0.4,0.5);
        //2.设置渲染窗口尺寸并开始渲染
        renWin.SetSize(450, 450);
        renWin.Render();
        //3.初始化交互器并真正开始
        vtkInteractorStyleTrackballCamera viewStyle = new vtkInteractorStyleTrackballCamera();
        iren.SetInteractorStyle(viewStyle);
        iren.Initialize();
        iren.Start();

    }

    private void setOperationToUnion(){

    }


}
