package simulation;


import genome.Brain;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;

public class Life extends PApplet {

    public Agent newBorn() {
        Agent a = new Agent(random(Width), random(Height));

        a.mutate();
        a.brain.getGenome().mutate();

        return a;
    }

    private class Agent implements Cloneable {

        /// steering ///
        public PVector pos;
        public float angle;
        public float speed;
        public boolean turbo = false;

        /// brain, genes ///
        public Brain brain;
        public int inNodes, outNodes;
        public float sightRange;
        public float energy = 100;

        public int mutationSize;
        public float mutationPropab;

        /// apperance ///
        public float size;
        public int r, g, b;

        //public int index;
        public boolean dead = false;
        public boolean egg = false;

        Agent(float x, float y) {
            //System.out.println(x + " -- " + y);
            inNodes = 12;
            outNodes = 5;
            pos = new PVector(x, y);
            angle = random(360);
            speed = random(0.5f, 1.5f);
            size = random(5, 15);

            //energy = random(90, 110);

            brain = new Brain(inNodes, outNodes);
            System.gc();

            sightRange = random(200, 500);

            mutationSize = (int) random(2, 20);
            mutationPropab = (float) Math.random() / 10000;

            r = (int) random(255);
            g = (int) random(255);
            b = (int) random(255);

        }


        private float[] distAngleFoodAgent(ArrayList<Agent> agents, ArrayList<Food> food) {

            float d = pos.dist(agents.get(0).getPos());
            float angle = PVector.angleBetween(pos, agents.get(0).getPos());
            float agentsSeen = 0;
            for (Agent a : agents) {
                if (pos.dist(a.getPos()) <= d) {
                    d = pos.dist(a.getPos());
                    angle = PVector.angleBetween(pos, a.getPos());
                }

                if (pos.dist(a.getPos()) < sightRange) agentsSeen++;

            }
            float d1 = 0;
            if (food.size() != 0) {
                d1 = pos.dist(food.get(0).getPos());
            }
            float angle1 = PVector.angleBetween(pos, agents.get(0).getPos());
            float foodSeen = 0;
            for (Food f : food) {
                if (pos.dist(f.getPos()) <= d1) {
                    d1 = pos.dist(f.getPos());
                    angle1 = PVector.angleBetween(pos, f.getPos());
                }

                if (pos.dist(f.getPos()) < sightRange) foodSeen++;
            }

            if (d > sightRange) {
                d = 0;
                angle = 0;
            }
            if (d1 > sightRange) {
                d1 = 0;
                angle1 = 0;
            }

            float sp = turbo ? speed * 3 : speed;


            return new float[]{d, angle, d1, angle1, agentsSeen, foodSeen, sp};

        }

        private void forward() {
            PVector v;
            if (turbo) {
                v = vectorFromAngle(angle, 3 * speed);
                energy -= 1;
            } else {
                v = vectorFromAngle(angle, speed);
            }

            pos.add(v);

            energy -= 0.01;
        }

        private void backward() {
            PVector v = vectorFromAngle(angle, speed).mult(-1);

            pos.add(v);
            energy -= 0.01;
        }

        private void left() {
            angle += 1;
            energy -= 0.01;
        }

        private void right() {
            angle -= 1;
            energy -= 0.01;
        }


        public void update(ArrayList<Agent> agents, ArrayList<Food> food) {
            edges();
            think(agents, food);
            //forward();
            eat(food);

            if (Math.random() < mutationPropab) {
                mutate();
            }

            energy -= (2 * speed) / size / 10;


            //if (frameCount % 100 * index == 0) brain.getGenome().mutate();

            if (energy <= 0) dead = true;


        }

        public void change(Agent a) {
            angle = random(360);
            size += Math.random() / 10;
            brain = (Brain) a.brain.clone();
            brain.getGenome().mutate();
            brain.getGenome().mutate();

            r = (int) random(255);
            g = (int) random(255);
            b = (int) random(255);
        }

        public void mutate() {
            //System.out.println("gyruewqfbywqfgwqfgbwuyq");
            r = Math.random() < mutationPropab ? (int) (r + random(-mutationSize, mutationSize)) : r;
            g = Math.random() < mutationPropab ? (int) (g + random(-mutationSize, mutationSize)) : g;
            b = Math.random() < mutationPropab ? (int) (b + random(-mutationSize, mutationSize)) : b;

            speed = Math.random() < mutationPropab ? (int) (speed + random(-mutationSize, mutationSize)) : speed;
            size = Math.random() < mutationPropab ? (int) (size + random(-mutationSize, mutationSize)) : size;

            sightRange = Math.random() < mutationPropab ? (int) (sightRange + random(-mutationSize, mutationSize)) : sightRange;

            if (Math.random() < mutationPropab) brain.getGenome().mutate();
        }

        public Agent crossover() {

            Agent a = new Agent(pos.x, pos.y);
            a.brain = (Brain) brain.clone();
            a.brain.setGenome(brain.getGenome().clone());
            a.brain.getGenome().mutate();


            a.r = r + (int) random(-mutationSize, mutationSize);
            a.g = g + (int) random(-mutationSize, mutationSize);
            a.b = b + (int) random(-mutationSize, mutationSize);

//            a.r = (int) random(255);
//            a.g = (int) random(255);
//            a.b = (int) random(255);

            a.size = (float) (size + (Math.random() * 2 - 1) / mutationSize);
            a.speed = (float) (speed + (Math.random() * 2 - 1) / mutationSize);


            return a;
//            Agent newAgent = new Agent(pos.x, pos.y);
//            try {
//                Agent newA = (Agent) clone();
//                newA.change(this); ////////////// importatnt //////////////////
////                newA.brain.getGenome().mutateLink();
////                newA.brain.getGenome().mutate();
////                newA.brain.getGenome().mutate();
//                return newA;
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//                System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOOOOOORrrrrrrrrrrr");
//                return null;
//            }

            /////////////////////////////////////////////////////////////


//            Agent newAgent = null;
//            try {
//                newAgent = (Agent) clone();
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//
//            newAgent.size = Math.random() < 0.5 ? size : size + random(1) / 100;
//            newAgent.angle = random(360);
//            newAgent.pos = pos.copy();
//            // newAgent.pos = new PVector(random(Width), random(Height));
//            newAgent.sightRange = Math.random() < 0.5 ? sightRange : sightRange + random(1);
//            newAgent.speed = Math.random() > 0.5 ? speed : speed + random(1) / 100;
//            newAgent.r = Math.random() < 0.5 ? r : r + (int) random(1);
//            newAgent.g = Math.random() < 0.5 ? g : g + (int) random(1);
//            newAgent.b = Math.random() < 0.5 ? b : b + (int) random(1);
//
//            newAgent.brain = (Brain) brain.clone();
//
//            newAgent.brain.getGenome().mutateLink();
//            newAgent.brain.getGenome().mutate();
//            newAgent.brain.getGenome().mutate();
//
//
//            return newAgent;

        }

        public void think(ArrayList<Agent> agents, ArrayList<Food> food) {
            float[] arr = new float[inNodes];
            int count = 0;

            float[] arr1 = distAngleFoodAgent(agents, food);
            for (int i = 0; i < arr1.length; i++) {
                arr[i] = arr1[i];
                count++;
            }
            arr[count] = r;
            count++;
            arr[count] = g;
            count++;
            arr[count] = b;
            count++;
            arr[count] = energy;
            count++;
            arr[count] = day ? 1f : 0f;
            count++;

            arr = brain.getGenome().calculate2(arr);


            if (arr[0] > arr[1] && arr[0] >= 0.5) forward();
            else if (arr[0] < arr[1] && arr[1] >= 0.5) backward();

            if (arr[2] > arr[3] && arr[2] >= 0.5) left();
            else if (arr[2] < arr[3] && arr[3] >= 0.5) right();

//            if (arr[0] >= 0.5) {
//                forward();
//            }
//            if (arr[1] >= 0.5) {
//                backward();
//            }
//            if (arr[2] >= 0.5) {
//                left();
//            }
//            if (arr[3] >= 0.5) {
//                right();
//            }
            if (arr[4] >= 0.5) {
                egg();
            }
        }


        public void eat(ArrayList<Food> food) {
            turbo = energy > 200;

            ArrayList<Food> fo = new ArrayList<>();
            for (Food f : food) {
                float d = pos.dist(f.getPos());
                if (d <= size) {
                    energy += 40;
                    fo.add(f);
                }
            }

            for (Food f : fo) f.eat();

        }

        public void egg() {
            if (energy > 100) {
                egg = true;
                energy -= 70;
            }
        }

        public void edges() {

            if (pos.x < 0 || pos.x > Width) {
                angle = (360f - angle) % 360;
            }

            if (pos.y < 0 || pos.y > Height) {
                angle = (180f - angle) % 360;
            }

//            if (pos.x < 0) pos.x = Width;
//            else if (pos.x > Width) pos.x = 0;
//
//            if (pos.y < 0) pos.y = Height;
//            else if (pos.y > Height) pos.y = 0;
        }

        public void show() {

            stroke(0);
            fill(r, g, b);


            float x1 = size * sin(radians(this.angle)) + this.pos.x;
            float x2 = size * sin(radians(this.angle + 155)) + this.pos.x;
            float x3 = size * sin(radians(this.angle + 205)) + this.pos.x;

            float y1 = size * cos(radians(this.angle)) + this.pos.y;
            float y2 = size * cos(radians(this.angle + 155)) + this.pos.y;
            float y3 = size * cos(radians(this.angle + 205)) + this.pos.y;

            beginShape();

            vertex(x1, y1);
            vertex(x2, y2);
            vertex(x3, y3);
            vertex(x1, y1);

            endShape();
        }

        public void debug() {
            stroke(r, g, b);
            noFill();
            ellipse(pos.x, pos.y, sightRange, sightRange);

        }

        public PVector getPos() {
            return pos;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private class Food {

        private PVector pos;
        private int size = 5;
        private int eaten = 100;

        Food(float x, float y) {
            pos = new PVector(x, y);
        }


        public void show() {
//            if (day)
//                stroke(0);
//            else
//                stroke(255);
            stroke(map(dayCount, 50, 220, 220, 50));
            fill(12, 50, 10);

            ellipse(pos.x, pos.y, size * eaten / 100, size * eaten / 100);
        }

        public void eat() {
            if (area.reduce() < area.pellets.size() && area.reduce() < 250) {
                area.pellets.remove(this);
            } else {
                pos = new PVector(random(Width), random(Height));
            }
        }


        public PVector getPos() {
            return pos;
        }
    }


    private class Area {
        private int numOfPellets;
        private ArrayList<Food> pellets;
        private int i = 0;
        private float k = 0;

        Area(int numOfPellets) {
            this.numOfPellets = numOfPellets;
            pellets = new ArrayList<>();
            for (int i = 0; i < numOfPellets; i++) {
                pellets.add(new Food(random(Width), random(Height)));
            }
        }

        public void display() {

//            try {
//                i %= pellets.size();
//                pellets.get(i).eat();
//            } catch (Exception e) {
//                return;
//            }
//            i++;
//            i %= pellets.size();
            for (Food f : pellets) {
                f.show();
            }
        }

        public int reduce() {
            //float num = 100 * (200 / flock.agents.size());
            //k = k * (float)Math.exp((200 - flock.agents.size()) / (200 - 100));
            //float num = k * 100 * (200 / flock.agents.size());
            float num = (float) agentsNum * ((float) agentsNum / (float) flock.agents.size());

            if (pellets.size() < num && num < 250) {
                for (int i = 0; i < num - pellets.size(); i++) {
                    //pellets.remove(random(pellets.size() - 1));
                    //System.out.println("more");
                    pellets.add(new Food(random(Width), random(Height)));
                }
            }

//            if (pellets.size() > num) {
//                for (int i = 0; i < pellets.size() - num; i++) {
//                    //System.out.println("less");
//                    pellets.remove(pellets.get((int) random(pellets.size() - 1)));
//                }
//            } else {
//                for (int i = 0; i < num - pellets.size(); i++) {
//                    //pellets.remove(random(pellets.size() - 1));
//                    //System.out.println("more");
//                    pellets.add(new Food(random(Width), random(Height)));
//                }
//            }

            return (int) num;
        }

        public ArrayList<Food> getPellets() {
            return pellets;
        }
    }

    private class Flock {

        private int quantity;
        private ArrayList<Agent> agents;

        private boolean spawn = true;

        Flock(int quantity) {
            this.quantity = quantity;
            agents = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                agents.add(new Agent((float) Math.random() * Width, random(0, 1) * Height));
            }
        }

        public void update() {
            ArrayList<Agent> queue = new ArrayList<>();
            for (Agent a : agents) {
                a.update(agents, area.getPellets());
                if (a.dead || a.egg) {
                    queue.add(a);
                }

//                if (Math.random() < 0.001) {
//                    a = a.crossover();
//                }
            }

            if (spawn && agents.size() < 50) {
                if (Math.random() <= 0.05) {
                    agents.add(newBorn());
                }
            } else if (spawn && agents.size() > 50) {
                spawn = false;
            } else {
                if (Math.random() <= 1 / newFreq) {
                    agents.add(newBorn());
                }
            }

//            if (frameCount % 500 == 0) {
//                Agent n = agents.get((int) (Math.random() * (agents.size() - 1))).crossover();
//                if (n != null)
//                    agents.add(n);
//            }

            for (Agent a : queue) {
                if (a.dead) {
                    agents.remove(a);
                    continue;
                }
                if (a.egg) {
                    a.egg = false;
                    agents.add(a.crossover());

                    System.out.println("Added");
                }
            }
        }

        public void display() {
            for (Agent a : agents) {
                a.show();
            }
        }

    }

    private PVector vectorFromAngle(float angle, float mag) {
        float x = mag * sin(radians(angle));
        float y = mag * cos(radians(angle));

        PVector v = new PVector(x, y);
        return v;
    }


    private Flock flock;
    private Area area;

    private int Width;
    private int Height;

    private int firstW, firstH;


//    float scaleFactor = 1.0f;
//    float translateX = 0.0f;
//    float translateY = 0.0f;


    private boolean debug = true;

    private Agent follow;


    private int agentsNum = 50;
    private int newFreq = 300;

    private boolean day = true;
    private boolean dayCH = false;
    private float dayCount = 220;
    private int dayTime = 2000;
    private float dayDiff = 500f / dayTime;

    public void settings() {
        //size(800, 600);

        fullScreen();
        firstW = width;
        firstH = height;

        Width = 1920;
        Height = 1080;


    }

    public void setup() {


        flock = new Flock(agentsNum / 2);
        area = new Area(25);


        flock.display();
    }

    public void dayChange() {
        if (day) {
            dayCount -= dayDiff;
        } else {
            dayCount += dayDiff;
        }

        if (dayCount <= 20 || dayCount >= 220) {
            dayCH = false;
            day = !day;
        }
    }

    public void draw() {
        background(dayCount);

        //debug = mouseX < 20 && mouseY < 20;


//        if (scale > 0) {
//            scale(scale, scale);
//        } else if (scale < 0) {
//            scale(-1 / scale, -1 / scale);
//        }


        if (frameCount % dayTime == 0) {
            //mark = frameCount;
            dayCH = true;

            //System.out.println("change");
        }

        if (dayCH) {
            dayChange();
        }

        if (follow != null && flock.agents.contains(follow)) {
            follow.debug();
            fill(255, 0, 0);
            text("Size: " + follow.size, 10, 20);
            text("Speed: " + follow.speed, 10, 40);
            text("Sight range: " + follow.sightRange, 10, 60);
            text("Hidden nodes in brain: " + (follow.brain.getGenome().getNodes().size() - (follow.inNodes + follow.outNodes)), 10, 80);
            text("Connections in brain: " + follow.brain.getGenome().getConnections().size(), 10, 100);
            text("Mutation probability: 1/" + 1f / follow.mutationPropab, 10, 120);
        }


//        fill(255, 0, 0);
//        text(area.pellets.size(), 10, 10);
//        text(flock.agents.size(), 10, 30);
//        text((float) agentsNum * ((float) agentsNum / (float) flock.agents.size()), 10, 50);


//        text(scaleFactor, 10, height - 15);
//        text(translateX, 10, height - 35);
//        text(translateY, 10, height - 25);
//
//        translate(translateX, translateY);
//        scale(scaleFactor);


        area.display();
        area.reduce();

        flock.update();
        flock.display();

        //System.out.println((100f*(200f / flock.agents.size())));


    }

    public void mousePressed() {
        Agent aa = follow;
        float d = Float.POSITIVE_INFINITY;
        for (Agent a : flock.agents) {
            float ddd = dist(a.pos.x, a.pos.y, mouseX, mouseY);
            if (ddd < d && ddd < a.size) {
                d = ddd;
                follow = a;
            }
        }
        if (follow == aa) {
            follow = null;
        }
        if (!flock.agents.contains(follow)) {
            follow = null;
        }
    }

//    public void mouseWheel(MouseEvent e) {
//        if (e.getAmount() / 50 < 0) {
//            scale *= 2;
//        } else {
//            scale *= 0.5;
//        }
//    }


//    public void mouseWheel(MouseEvent e) {
//        translateX -= mouseX;
//        translateY -= mouseY;
//        float delta = (float) (e.getCount() > 0 ? 1.05 : e.getCount() < 0 ? 1.0 / 1.05 : 1.0);
//        scaleFactor *= delta;
//        //scaleFactor = constrain(scaleFactor, scaleFactor*Width/1920, 20);
//        scaleFactor = constrain(scaleFactor, (float) width / Width, 10);
//        translateX *= delta;
//        translateY *= delta;
//        translateX += mouseX;
//        translateY += mouseY;
//
//        translateX = constrain(translateX, -width, 0);
//        translateY = constrain(translateY, -height, 0);
//
//        //translateX = translateX > 0 ? 0 : translateX;
//        //translateY = translateY > 0 ? 0 : translateY;
//    }
//
//    public void keyPressed() {
//        if (key == 'r') {
//            scaleFactor = 1f;
//            translateX = 0.0f;
//            translateY = 0.0f;
//        }
//    }
//
//    public void mouseDragged(MouseEvent e) {
//        translateX += mouseX - pmouseX;
//        translateY += mouseY - pmouseY;
//
//        translateX = constrain(translateX, -width, 0);
//        translateY = constrain(translateY, -height, 0);
//
////        translateX = translateX > 0 ? 0 : translateX;
////        translateY = translateY > 0 ? 0 : translateY;
//
////        translateX = constrain(translateX, 0, scaleFactor * Width - Width);
////        translateY = constrain(translateY, 0, Height);
//    }


    public static void main(String[] args) {
        PApplet.main("simulation.Life", args);
    }
}
