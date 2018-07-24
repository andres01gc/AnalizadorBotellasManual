package gc;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

public class GestorImagenes {
    ListIterator<String> currentPathIterator;
    PApplet app = Main.app;
    PImage currentPImage;
    String currentPath;
    ArrayList<Integer> divisores = new ArrayList<>();
    String[] nombreDivisores = {"Fin de Tapa", "fin de hombros", "fin de cuerpo", "fin de base"};
    private BufferedWriter bufferedWriter;
    private int originalShapeHeight;
    private int originalShapeWidth;
    private float scaleFactor;

    public GestorImagenes(ArrayList<String> pathImagenes) {
        currentPathIterator = pathImagenes.listIterator();
        // iniciar el iterador
        loadAll();

    }

    public void analizarPath() {


    }

    public void drawCurrentImage() {
        app.image(currentPImage, 0, 0);
        //pintar linea horizotal que guia la posicio´n del mouse sobre la imagen
        app.stroke(255, 0, 0, 50);
        app.line(0, app.mouseY, app.width, app.mouseY);


        //pintar los divisores
        for (Integer v : divisores) {
            app.stroke(0, 255, 0, 250);
            app.line(0, v, app.width, v);
        }
    }

    public void next() {
        if (currentPathIterator.hasNext()) {
            clearLines();
            currentPath = currentPathIterator.next();
            resizeCurrentPApplet();
        } else {
            System.out.println("no hay mas imagenes");
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void back() {
        if (currentPathIterator.hasPrevious()) {
            clearLines();
            currentPath = currentPathIterator.previous();
            resizeCurrentPApplet();
        } else {
            System.out.println("no hay mas imagenes");
        }
    }

    public void addLine() {
        divisores.add(app.mouseY);
        validarCompletado();
    }

    private void validarCompletado() {
        if (divisores.size() > 2) {
            guardarDatos();
            next();
        }
    }

    public void iniciarArchivo() {
        // The name of the file to open.
        String fileName = "E:\\Gc\\Trabajo\\Arce\\Botellas\\Codes\\Java\\AnalizadorSiluetasLight\\data\\datos_partes_botellas.csv";

        try {
            new File(fileName).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("aja");
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println("se inicia el buffer");
            // Note that write() does not automatically
            // append a newline character.

            //agrego primero los titulos

            String[] titulos = {"nombre imagen", "categoria", "altura_px de botella", "fin de la tapa", "fin del cuello", "fin del cuerpo", "% tapa", "% cuello", "% cuerpo", "% base"};

            String primeraLinea = "";
            for (String titulo : titulos) {
                primeraLinea += titulo + ",";
            }

            bufferedWriter.write(primeraLinea);
            bufferedWriter.newLine();

            // Always close files.

        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

    private void guardarDatos() {
        try {
            String[] s = currentPath.split("\\\\");

            String nombreImagen = s[s.length - 1].split(".JPG")[0];
            String categoria = s[s.length - 2];

            String alturaBotellaInPx = String.valueOf(originalShapeHeight);

            String findelatapa = String.valueOf(divisores.get(0) * scaleFactor);
            String findelcuello = String.valueOf(divisores.get(1) * scaleFactor);
            String findelcuerpo = String.valueOf(divisores.get(2) * scaleFactor);

//            String[] titulos = {"nombre imagen", "categoria", "altura_px de botella", "fin de la tapa", "fin del cuello", "fin del cuerpo", "% tapa", "% base", "suma Delta de Pendiente de Cuerpo"};

            float pr_tapa = ((divisores.get(0) * scaleFactor) * 100) / originalShapeHeight;

            float pr_cuello = (((divisores.get(1) * scaleFactor) * 100) / originalShapeHeight) - pr_tapa;
            float pr_cuerpo = (((divisores.get(2) * scaleFactor) * 100) / originalShapeHeight) - pr_tapa - pr_cuello;
            float pr_base = 100 - pr_tapa - pr_cuello - pr_cuerpo;

            String porc_tapa = String.valueOf(pr_tapa);
            String porc_cuello = String.valueOf(pr_tapa);
            String porc_Cuerpo = String.valueOf(pr_cuerpo);
            String porc_Base = String.valueOf(pr_base);


            String[] datos = {nombreImagen, categoria, alturaBotellaInPx, findelatapa, findelcuello, findelcuerpo, porc_tapa, porc_cuello, porc_Cuerpo, porc_Base};

            //TODO agregar los datos a datos

            String line = "";

            for (String dat : datos) {
                line += dat + ",";
            }

            bufferedWriter.write(line);
            System.out.println("se escribe");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finalizar() throws IOException {
        bufferedWriter.close();
    }

    public void clearLines() {
        divisores.clear();
    }

    public void loadAll() {
        //cargo la primera imagen que se va a mostrar
        if (currentPathIterator.hasNext()) {
            currentPath = currentPathIterator.next();
            resizeCurrentPApplet();
        }
        iniciarArchivo();
    }

    public PImage cropBinarizedImage(PImage source) {
        PImage image = source.get();

        int posX = 0, posY = 0;// esquina superior del recorte
        int ancho = 0, alto = 0;

        image.loadPixels();

        int umbral = 250;
        int salto = 1;

        boolean continuar = true;

        //recorres parte superior
        for (int y = 0; y < image.height; y += salto) {
            for (int x = 0; x < image.width; x += salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > 200) {
                    posY = y;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }


        continuar = true;
        //recorrer parte izquierda
        for (int x = 0; x < image.width; x += salto) {
            for (int y = 0; y < image.height; y += salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    posX = x;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

        continuar = true;
        for (int x = image.width - 1; x > 0; x -= salto) {
            for (int y = image.height - 1; y > 0; y -= salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    ancho = x - posX + 0;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;

        }
        continuar = true;
        for (int y = image.height - 1; y > 0; y -= salto) {
            for (int x = image.width - 1; x > 0; x -= salto) {
                int posPx = x + (image.width * y);
                if (app.brightness(image.pixels[posPx]) > umbral) {
                    alto = y - posY;
                    continuar = false;
                    break;
                }
            }
            if (!continuar) break;
        }

//        System.out.println("posX: " + posX + " " + "posY: " + posY + " " + "w: " + ancho + " " + "h: " + alto);
        PImage resultado = image.get(posX, posY, ancho, alto);
        image.updatePixels();

        return resultado;
    }

    public void resizeCurrentPApplet() {
        PImage selectedImage = cropBinarizedImage(app.loadImage(currentPath));

        originalShapeWidth = selectedImage.width;
        originalShapeHeight = selectedImage.height;

//        int currentWidthScreen = app.displayWidth;

        int currentHeightScreen = app.displayHeight;

        // buscar el lado mas grande de la imagen
        float nuevoWidth = 0;
        float nuevoHeight = 0;

        scaleFactor = 0;
        if (originalShapeHeight > originalShapeWidth) {
            System.out.println("imah " + originalShapeHeight + " pantallaH: " + currentHeightScreen);

            scaleFactor = (float) originalShapeHeight / (float) currentHeightScreen;
            scaleFactor *= 1.15;
            nuevoWidth = originalShapeWidth / scaleFactor;
            nuevoHeight = originalShapeHeight / scaleFactor;

        } else {
        }

        app.getSurface().setSize((int) nuevoWidth, (int) nuevoHeight);
        System.out.println("el scale es " + scaleFactor);

        selectedImage.resize((int) nuevoWidth, (int) nuevoHeight);
        currentPImage = selectedImage;

    }
}
