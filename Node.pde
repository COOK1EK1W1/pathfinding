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
    
    void tracepath(){
        fill(0,100,255);
        rect(this.position.x * scale, this.position.y * scale, scale, scale);
        if (this.parent != null){this.parent.tracepath();}
    }
    
    void drawsquare(){
        int textscale = 10;
        int offset = scale / 2;
        
        if (closed){fill(255,100,0);}else{fill(100,255,0);}
        
        rect(this.position.x * scale, this.position.y * scale,scale,scale);
        fill(0);
        textSize(textscale);
        if (costs){
            text(str(int(this.Gcost)), this.position.x * scale + textscale, this.position.y * scale + textscale);
            text(str(int(this.Hcost)), this.position.x * scale + textscale * 3, this.position.y * scale + textscale);
            text(str(int(this.Fcost)), this.position.x * scale + textscale, this.position.y * scale + textscale * 3);}    
        if (parent != null && parentarrows){
            line(this.position.x * scale + offset, this.position.y * scale + offset, 
            ((parent.position.x - this.position.x) * 0.4 + this.position.x) * scale + offset, ((parent.position.y - this.position.y) * 0.4 + this.position.y)* scale + offset);
        }
    }


}
