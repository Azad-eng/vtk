package com.ryl.vtk.vtkBeginner.drawACode;

import vtk.*;

/**
 * @author: EFL-ryl
 */
public class Code {
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
        //CREATE A CONE
        //1.创建一个vtkConeSource的实例
        vtkConeSource cone = new vtkConeSource();
        //2.设置基本属性
        cone.SetHeight(5.0);
        cone.SetRadius(2.0);
        cone.SetResolution(8);

        //CREATE COLORS
        //1.颜色属性
        vtkNamedColors coneNamedColors = new vtkNamedColors();
        //2.actor颜色
        double[] actorColor = new double[4];
        coneNamedColors.GetColor("MediumVioletRed", actorColor);
        //3.背景颜色
        double[] bgColor = new double[4];
        coneNamedColors.GetColor("Wheat", bgColor);

        //CREATE A MAPPER AND A ACTOR
        //1.创建vtkPolyDataMapper并且映射cone source得到一个相关的mapper（映射器）
        vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
        coneMapper.SetInputConnection(cone.GetOutputPort());
        //2.创建一个actor并且分配mapper
        vtkActor coneActor = new vtkActor();
        coneActor.SetMapper(coneMapper);
        //3.设置actor颜色属性
        coneActor.GetProperty().SetColor(actorColor);

        //Create the renderer, render window and interactor
        //1.创建一个render（渲染器）并且添加actor
        vtkRenderer ren = new vtkRenderer();
        ren.AddActor(coneActor);
        //2.创建一个render window（渲染窗口） 并添加render
        vtkRenderWindow renWin = new vtkRenderWindow();
        renWin.AddRenderer(ren);
        //3.创建一个window interactor（窗口交互器）并添加window
        vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);

        //Visualise the cone
        //1.设置渲染器背景色
        ren.SetBackground(bgColor);
        //2.设置渲染窗口尺寸并开始渲染
        renWin.SetSize(300, 300);
        renWin.Render();
        //3.初始化交互器并真正开始
        vtkInteractorStyleTrackballCamera viewStyle = new vtkInteractorStyleTrackballCamera();
        iren.SetInteractorStyle(viewStyle);
        iren.Initialize();
        iren.Start();
    }
}
