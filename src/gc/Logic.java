package gc;

import processing.core.PApplet;

import java.io.File;
import java.util.ArrayList;

public class Logic {
    private final GestorImagenes gestor;
    private String mainPath = "E:\\Gc\\Trabajo\\Arce\\Botellas\\Codes\\Resultados\\binaries";
    ArrayList<String> pathImagenes = new ArrayList();


    public Logic(PApplet app) {
        pathImagenes = cargarPathsImagenesWithoutSubFolders(mainPath);
        gestor = new GestorImagenes(pathImagenes);
        //  gestor.loadAll();
    }

    public void draw() {
        gestor.drawCurrentImage();
    }

    public void mPressed() {
        // TODO aquí irá el que se encargará de gestionar las p osiciones del mouse

        if (Main.app.mouseButton == Main.app.LEFT) {
            gestor.addLine();
        }

        if (Main.app.mouseButton == Main.app.RIGHT) {
            gestor.clearLines();
        }
    }

    public ArrayList<String> cargarPathsImagenesWithoutSubFolders(String path) {
        File carpetaSeleccionada = new File(path);
        ArrayList<String> paths = new ArrayList<>();

        //se encarga de guardar acumulado_todo los paths de todas las imagnes dentro de la carpeta.
        for (File carpetaCat : carpetaSeleccionada.listFiles()) {
            System.out.println(carpetaCat.getAbsolutePath());
            for (File imagen : carpetaCat.listFiles()) {
                if (imagen.getName().contains(".JPG")) {
                    paths.add(imagen.getAbsolutePath());
                    // se cargan cada paths de la carpeta selecciones
                }
            }
        }
        return paths;
    }

    public void kPresed() {
        System.out.println(Main.app.key);

        if (Main.app.keyCode == Main.app.LEFT) {
            gestor.back();
        }

        if (Main.app.keyCode == Main.app.RIGHT) {
            gestor.next();
        }
    }


}

