import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class pathfinding extends PApplet {

int scale = 25;
int bar_height = 50;
int mode;
int buttons = 6;
int state;
int[][] board;
boolean parentarrows = false;
boolean costs = false;
boolean step = false;
PVector start;
PVector end;
PVector[] neighbors = {new PVector( 1, 1), new PVector( 0, 1), new PVector(-1, 1), new PVector(-1, 0),
                       new PVector(-1,-1), new PVector( 0,-1), new PVector( 1,-1), new PVector( 1, 0)};
ArrayList<Node> open = new ArrayList<Node>();
ArrayList<Node> closed = new ArrayList<Node>();

public void setup(){
    start = new PVector(-1,-1);
    end = new PVector(-1,-1);
    mode = 1;
    state = 0;
    background(255);
    
    for (int x = 0; x < width / scale; x++){line(x*scale,0,x*scale,height - bar_height);}
    for (int y = 0; y < ((height - bar_height) / scale) + 1; y++){line(0,y*scale,width,y*scale);}
    
    board = new int[height - bar_height][width];
    
    for (int x = 0;x < buttons; x++){line(width / buttons * x, height - bar_height, width / buttons * x, height);}
    update_bar();
}

public void draw(){
    if (state == 1 || step){
        if (open.size() == 0){println("no path");}
        Node lowest = open.get(0);
        for (int i = 0; i < open.size(); i++){
            if (open.get(i).Fcost < lowest.Fcost || (open.get(i).Fcost == lowest.Fcost && open.get(i).Hcost < lowest.Hcost)){
                lowest = open.get(i);
            }
        }
        Node current = lowest;
        open.remove(open.indexOf(current));
        closed.add(current);
        current.closed = true;
        current.drawsquare();
        //<>//
        if (current.position.x == end.x && current.position.y == end.y){
            state = 2;
            current.tracepath();
            open = new ArrayList<Node>();
            closed = new ArrayList<Node>(); //<>//
            update_bar();
            
        }else
        for(int neighbor = 0;neighbor < 8;neighbor++){
            PVector neighborcoords = new PVector(current.position.x + neighbors[neighbor].x, current.position.y + neighbors[neighbor].y);
            boolean inclosed = false;
            for(int node = 0;node < closed.size();node++){
                if (closed.get(node).position.x == neighborcoords.x && closed.get(node).position.y == neighborcoords.y){inclosed = true;}
            }
            
            boolean closedcorner = false;   
            ////corner cutting logic goes here
            
            if (neighborcoords.x < 0 || 
            neighborcoords.x > (width - 1) / scale || 
            neighborcoords.y < 0 || 
            neighborcoords.y > (height - bar_height - 1) / scale ||
            board[PApplet.parseInt(neighborcoords.y)][PApplet.parseInt(neighborcoords.x)] == 1 ||
            closedcorner ||
            inclosed
            ){}else
            {
                boolean notinopen = true;
                for(int node = 0;node < open.size();node++){
                    if (open.get(node).position.x == neighborcoords.x && open.get(node).position.y == neighborcoords.y){notinopen = false;}
                }
                if (notinopen){
                    Node newnode = new Node(neighborcoords.x, neighborcoords.y, current);
                    open.add(newnode);
                    
                    newnode.drawsquare();

                }else{
                    for(int node = 0; node < open.size(); node++){
                        float newGcost = current.Gcost;
                        if (neighbor % 2 == 0){newGcost+=14;}else{newGcost+=10;}
                        if (open.get(node).position.x == neighborcoords.x && open.get(node).position.y == neighborcoords.y &&
                        open.get(node).Gcost > newGcost){
                            open.get(node).Gcost = newGcost;
                            open.get(node).Fcost = open.get(node).Gcost + open.get(node).Hcost;
                            open.get(node).parent = current;
                            open.get(node).drawsquare();
                        }
                    }
                }
            }
        } 
    }
    if (step){step = false;}
}

public void mouseDragged(){mous_has_done_soething(0);}
public void mousePressed(){mous_has_done_soething(1);}
public void mous_has_done_soething(int type){
    if (mouseY < height - bar_height){
        if (mode == 2){
            if (start.x != -1){board[PApplet.parseInt(start.y)][PApplet.parseInt(start.x)] = 0;}
            //board[mouseY / scale][mouseX / scale] = 2;
            fill(255,255,255);
            square(start.x * scale, start.y * scale, scale);
            start.x = PApplet.parseInt(mouseX / scale); 
            start.y = PApplet.parseInt(mouseY / scale);
        }
        if (mode == 3){
            
            if (end.x != -1){board[PApplet.parseInt(end.y)][PApplet.parseInt(end.x)] = 0;}
            //board[mouseY / scale][mouseX / scale] = 3;
            fill(255,255,255);
            square(end.x * scale, end.y * scale, scale);
            end.x = PApplet.parseInt(mouseX / scale); 
            end.y = PApplet.parseInt(mouseY / scale);
        }
        
        if (mode == 0){
            fill(255,255,255);
            if (board[mouseY / scale][mouseX / scale] == 2){start = new PVector(-1,-1);}
            if (board[mouseY / scale][mouseX / scale] == 3){end = new PVector(-1,-1);}
            } else
            
        if (mode == 1){fill(0,0,0);} else
        if (mode == 2){fill(0,255,0);} else
        if (mode == 3){fill(0,0,255);}
        square(PApplet.parseInt(mouseX / scale) * scale, PApplet.parseInt(mouseY / scale) * scale, scale);
        board[mouseY / scale][mouseX / scale] = mode;
    } else
    
    if (mouseX < width / buttons * 1 && type==1){mode++; if (mode==4){mode = 0;}} else
        
    if (mouseX < width / buttons * 2 && type==1 ){run(false);}else
    if (mouseX < width / buttons * 3 && type==1){run(true);}else
    
    if (mouseX < width / buttons * 4 && type==1){
    if(costs){costs = false;}else{costs = true;}}else
    
    if (mouseX < width / buttons * 5 && type==1){
    if(parentarrows){parentarrows = false;}else{parentarrows = true;}}else
    
    if (mouseX < width / buttons * 6 && type==1){setup();}
    update_bar();
}

public void update_bar(){
    String temp = "";
    textSize(32);
    fill(255);
    textAlign(CENTER, CENTER);
    rect(0,height - bar_height, width, height);
    for (int x = 0;x < buttons; x++){line(width / buttons * x, height - bar_height, width / buttons * x, height);}
    fill(0);
    if (mode == 0){temp = "Earser";} else
    if (mode == 1){temp = "Wall";} else
    if (mode == 2){temp = "Start";} else
    if (mode == 3){temp = "Finnish";}
    text(temp, width / buttons * 0 + width / buttons / 2, height - bar_height / 2);
    
    if(state == 0){temp = "Run A*";}else
    if(state == 1){temp = "Running";} else
    if(state == 2){temp = "Finnished";}
    text(temp, width / buttons * 1 + width / buttons / 2, height - bar_height / 2);
    
    text("Step", width / buttons * 2 + width / buttons / 2, height - bar_height / 2);
    
    if(costs){fill(0,255,0);}else{fill(0);}
    text("Costs", width / buttons * 3 + width / buttons / 2, height - bar_height / 2);
    
    if(parentarrows){fill(0,255,0);}else{fill(0);}
    text("Arrows", width / buttons * 4 + width / buttons / 2, height - bar_height / 2);
    
    fill(0);
    text("Clear", width / buttons * 5 + width / buttons / 2, height - bar_height / 2);
    
}

public void run(boolean stepp){
    if ( start.x != -1 && end.x != -1){
        if (open.size() == 0){open.add(new Node(start.x, start.y));}
        if (stepp){step=true;}else{state=1;}
    }else{println("need start or end");}
}
class Node{
    float Gcost;
    float Hcost;
    float Fcost;
    boolean closed;
    PVector position;
    Node parent;
    
    Node(float x, float y, Node parent){
        this.position = new PVector(x, y);
        this.parent = parent;
        this.closed = false;
        float horiz = abs(parent.position.x - x);
        float vert = abs(parent.position.y - y);
        if (horiz == vert){this.Gcost = 14 + parent.Gcost;}else{this.Gcost = 10 + parent.Gcost;}
        horiz = abs(end.x - x);
        vert = abs(end.y - y);
        if (horiz == vert){this.Hcost = horiz * 14;}else
        if (horiz < vert){
            this.Hcost = horiz * 14 + (vert - horiz) * 10;
        }else{
            this.Hcost = vert * 14 + (horiz - vert) * 10;
        }
        this.Fcost = this.Hcost + this.Gcost;
        
    }
    
    Node(float x, float y){
        this.position = new PVector(x, y);
        this.Fcost = 0;
        this.closed = false;
    }
    
    public void tracepath(){
        fill(0,100,255);
         square(this.position.x * scale, this.position.y * scale, scale);
         if (this.parent != null){this.parent.tracepath();
         }
         
    }
    
    public void drawsquare(){
        int textscale = 10;
        int offset = scale / 2;
        
        if (closed){fill(255,100,0);}else{fill(100,255,0);}
        
        square(this.position.x * scale, this.position.y * scale,scale);
        fill(0);
        textSize(textscale);
        if (costs){
            text(str(PApplet.parseInt(this.Gcost)), this.position.x * scale + textscale, this.position.y * scale + textscale);
            text(str(PApplet.parseInt(this.Hcost)), this.position.x * scale + textscale * 3, this.position.y * scale + textscale);
            text(str(PApplet.parseInt(this.Fcost)), this.position.x * scale + textscale, this.position.y * scale + textscale * 3);}    
        if (parent != null && parentarrows){
            line(this.position.x * scale + offset, this.position.y * scale + offset, 
            ((parent.position.x - this.position.x) * 0.4f + this.position.x) * scale + offset, ((parent.position.y - this.position.y) * 0.4f + this.position.y)* scale + offset);
        }
    }


}
    public void settings() {  size(1000,800); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "pathfinding" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
