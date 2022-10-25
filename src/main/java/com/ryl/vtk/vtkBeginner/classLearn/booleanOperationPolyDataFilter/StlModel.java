package com.ryl.vtk.vtkBeginner.classLearn.booleanOperationPolyDataFilter;

import vtk.*;

/**
 * @author EFL-ryl
 */
public class StlModel {
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
        vtkNamedColors colors = new vtkNamedColors();
        double[] input1ActorColor = new double[3];
        colors.GetColor("Tomato", input1ActorColor);
        double[] input2ActorColor = new double[3];
        colors.GetColor("Mint", input2ActorColor);

        //todo getProgramParameters()
        String operation = args[0];
        String fn1 = args[1];
        String fn2 = args[2];
        vtkPolyData poly1 = readPolyData("1.stl");
        vtkTriangleFilter tri1 = new vtkTriangleFilter();
        tri1.SetInputData(poly1);
        vtkCleanPolyData clean1 = new vtkCleanPolyData();
        clean1.SetInputConnection(tri1.GetOutputPort());
        clean1.Update();
        vtkPolyData input1 = clean1.GetOutput();

        vtkPolyData poly2= readPolyData("2.stl");
        vtkTriangleFilter tri2 = new vtkTriangleFilter();
        tri2.SetInputData(poly2);
        vtkCleanPolyData clean2 = new vtkCleanPolyData();
        clean2.SetInputConnection(tri2.GetOutputPort());
        clean2.Update();
        vtkPolyData input2 = clean2.GetOutput();

        vtkPolyDataMapper input1Mapper = new vtkPolyDataMapper();
        input1Mapper.SetInputData(input1);
        input1Mapper.ScalarVisibilityOff();
        vtkActor input1Actor = new vtkActor();
        input1Actor.SetMapper(input1Mapper);
        input1Actor.GetProperty().SetDiffuseColor(input1ActorColor);
        input1Actor.GetProperty().SetSpecular(0.6);
        input1Actor.GetProperty().SetSpecularPower(20);
        input1Actor.SetPosition(input1.GetBounds()[1] - input1.GetBounds()[0], 0, 0);

        vtkPolyDataMapper input2Mapper = new vtkPolyDataMapper();
        input2Mapper.SetInputData(input2);
        input2Mapper.ScalarVisibilityOff();
        vtkActor input2Actor = new vtkActor();
        input2Actor.SetMapper(input2Mapper);
        input2Actor.GetProperty().SetDiffuseColor(input2ActorColor);
        input2Actor.GetProperty().SetSpecular(0.6);
        input2Actor.GetProperty().SetSpecularPower(20);
        input2Actor.SetPosition(-(input1.GetBounds()[1] - input1.GetBounds()[0]), 0, 0);

        vtkBooleanOperationPolyDataFilter booleanOperation = new vtkBooleanOperationPolyDataFilter();

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
