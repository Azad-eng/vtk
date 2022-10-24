package com.ryl.vtk.vtkBeginner.classLearn.booleanOperationPolyDataFilter;

import vtk.*;

/**
 * @author EFL-ryl
 */
public class StlModel {
    public static void main(String[] args) {
        //Create the renderer, render window and interactor
        //1.创建一个render（渲染器）并且添加actors
        vtkRenderer ren = new vtkRenderer();

        vtkActor unionActor = getBooleanOperationActor(-2.0, 0);
        ren.AddActor(unionActor);
        unionActor.Delete();

        vtkActor intersectionActor = getBooleanOperationActor(0.0, 1);
        ren.AddActor(intersectionActor);
        intersectionActor.Delete();

        vtkActor differenceActor = getBooleanOperationActor(2.0, 2);
        ren.AddActor(differenceActor );
        differenceActor.Delete();

        //2.创建一个render window（渲染窗口） 并添加render
        vtkRenderWindow renWin = new vtkRenderWindow();
        renWin.AddRenderer(ren);
        //3.创建一个window interactor（窗口交互器）并添加window
        vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);

        ren.Render();
        renWin.Start();
    }

    public static vtkActor getBooleanOperationActor(int operation){
        vtkSTLReader stlModel1 = new vtkSTLReader();
        stlModel1.SetFileName("a.stl");
        stlModel1.Update();

        vtkSTLReader stlReader2 = new vtkSTLReader();
        stlReader2.SetFileName("b.stl");
        stlReader2.Update();

        vtkPolyDataMapper mapper1 = new vtkPolyDataMapper();
        mapper1.SetInputConnection(stlReader2.GetOutputPort());
        vtkActor actor1 = new vtkActor();
        actor1.SetMapper(mapper1);
        actor1.SetPosition(-19, +3.5, -19);

        vtkBooleanOperationPolyDataFilter boolFilter = new vtkBooleanOperationPolyDataFilter();
        boolFilter.SetOperation(operation);
        boolFilter.SetInputConnection(0, stlModel1.GetOutputPort());
        boolFilter.SetInputConnection(1, stlReader2.GetOutputPort());

        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInputConnection(boolFilter.GetOutputPort());
        vtkActor actor = new vtkActor();
        actor.SetMapper(mapper);
        return actor;
    }

    public static vtkActor getBooleanOperationActor(double x, int operation){
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
}
