import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Window extends PApplet {

    double minx;
    double miny;
    double maxx;
    double maxy;

    double centerx = -0.761574;
    double centery = -0.0847596;

    double zoomQuotient = 0.99;
    double iterationsQuotient = 1.001;

    int iWidth = 1920;
    int iHeight = 1080;
    double aspectRatio = (double)iHeight / (double)iWidth;
    double xradius = 2;
    double yradius = xradius * aspectRatio;

    int framesTotal = 30*100;

    int maxiterations = 100;



    public void settings(){
        size(100,100);
    }

    public void setup(){

        for(int frameno = 1; frameno <= framesTotal; frameno++){
            minx = centerx - xradius;
            miny = centery - yradius;
            maxx = centerx + xradius;
            maxy = centery + yradius;

            PImage image = new PImage(iWidth,iHeight);
            buildMandelbrot(image,maxiterations,frameno).save("D:/mandelbrot/mandelbrot"+String.format("%05d",frameno)+".png");
            xradius *= zoomQuotient;
            yradius = xradius * aspectRatio;
            maxiterations = (int)Math.ceil(maxiterations * iterationsQuotient);
        }
        exit();

    }

    public void draw(){

        noLoop();
    }

    PImage buildMandelbrot(PImage image, int maxiterations,int frameno){
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                double a = map(x, 0, image.width, minx, maxx);
                double b = map(y, 0, image.height, miny, maxy);

                double ca = a;
                double cb = b;

                int n = 0;

                while (n < maxiterations) {
                    double aa = a * a - b * b;
                    double bb = 2 * a * b;
                    a = aa + ca;
                    b = bb + cb;
                    if (a * a + b * b > 16) {
                        break;
                    }
                    n++;
                }

                int pix = (x + y * image.width);
                if (n == maxiterations) {
                    image.pixels[pix] = color(0);
                } else {
                    image.pixels[pix] = getPixelColor(maxiterations,n);
                }


            }
            System.out.println("Processing column " + (x+1) + "/"+iWidth+". Frame " + frameno + "/" + framesTotal + " @ " + maxiterations + " per pixel");
        }
        image.updatePixels();
        return image;
    }

    public double map(double value, double start1, double stop1, double start2, double stop2) {
        double outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        String badness = null;
        if (outgoing != outgoing) {
            badness = "NaN (not a number)";
        } else if (outgoing == -1.0F / 0.0 || outgoing == 1.0F / 0.0) {
            badness = "infinity";
        }

        if (badness != null) {
            String msg = String.format("map(%s, %s, %s, %s, %s) called, which returns %s", nf((float)value), nf((float)start1), nf((float)stop1), nf((float)start2), nf((float)stop2), badness);
            PGraphics.showWarning(msg);
        }

        return outgoing;
    }

    int getPixelColor(int maxiterations, int iterationscount) {

        double quotient = iterationscount / maxiterations;
        double brightness = map(iterationscount, 0, maxiterations, 0, 1);
        brightness = map(Math.sqrt(brightness), 0, 1, 0, 255);
        if (quotient > 0.5) {
            return color((float)brightness,255, (float) brightness);
        } else {
            return color(0,(float)brightness,0);
        }

    }

}
