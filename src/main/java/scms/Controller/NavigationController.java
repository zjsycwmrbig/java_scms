package scms.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Demo1Application;

import java.io.*;
import java.util.LinkedList;

/**
 * @author seaside
 * 2023-03-22 15:59
 */
//@RestController
//@RequestMapping("/god")
public class NavigationController {
    Vertex[] vertexes = null; //共有126个顶点，但第一个顶点在json文件中编号为1，所以数组0号不用
    //@RequestMapping("/readmap")
    public void readMap() throws IOException {
        //从边的json中读取
        ObjectMapper Json = new ObjectMapper();
        Road[] roads = Json.readValue(new File("C:\\Users\\wwhb\\Desktop\\Edges.json"),Road[].class);
        //构造邻接表
        Vertex[] vertexesTemp = new Vertex[127]; //为与json文件同步，0号不用
        for (int i = 1; i <= 126; i++) {
            vertexesTemp[i] = new Vertex();
            vertexesTemp[i].setId(i);
        }
        Node tempNode;
        for (int i = 0; i < roads.length; i++) { //边的json文件读取后roads[]数组从0号开始，所以i = 0
            tempNode = vertexesTemp[roads[i].getFrom()].front;
            if(vertexesTemp[roads[i].getFrom()].front == null)
                vertexesTemp[roads[i].getFrom()].front = new Node(roads[i].getTo(),roads[i].getLength(),roads[i].getType());
            else{
                while(tempNode.getNext()!=null)
                    tempNode = tempNode.getNext();
                tempNode.setNext(new Node(roads[i].getTo(),roads[i].getLength(),roads[i].getType()));
            }
        }
        System.out.println("邻接表建立成功");
        //将邻接表序列化为一个文件
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\wwhb\\Desktop\\edges.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(vertexesTemp);
            objectOutputStream.close();
            fileOutputStream.close();
            System.out.println("序列化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //序列化和反序列化应该拆开到不同代码地方吧？？？？？？？
        //注意反序列化之前Vertex类和Node类都必须已经加载
        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\wwhb\\Desktop\\edges.ser"); //文件位置可能需要更改
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            vertexes = (Vertex[]) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            System.out.println("反序列化成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*for (int i = 1; i <vertexes.length ; i++) {
            if(vertexes[i].front != null){
                tempNode = vertexes[i].front;
                while(tempNode!=null)
                {
                    System.out.println(" "+tempNode.getDistance());
                    tempNode = tempNode.getNext();
                }
            }
        }*///输出每条边，以对照是否反序列化正确，后续可以删掉。。。。。
    }
    public String getShortedPath(int startNumber,int endNumber,int mode){
        //使用迪杰斯特拉算法得到最短路径
        //mode == 1 返回最短路径， mode == 2 时返回最短路径的长度
        if(startNumber == endNumber){
            System.out.println("起终点相同！");
            return "0";//这个返回值是给旅行商问题用的
        }
        String Path = String.valueOf(endNumber);
        int size = 127;
        boolean[] haveVisited = new boolean[size]; //记录某一点是否被访问过
        double[] distanceToAll = new double[size]; //dist[]数组，记录起点到其他点的距离，每次都会更新
        int[] whichVertexToMe = new int[size]; //记录到该数组下标对应点的顶点，用于倒序输出
        for (int i = 1; i < size; i++) {
            haveVisited[i] = false; //所有点初始都是未被访问状态
            distanceToAll[i] = Double.MAX_VALUE; //代替无穷值
            whichVertexToMe[i] = 0;  //初始化
        }
        Node tempNode = vertexes[startNumber].front;
        while(tempNode!=null) {
            distanceToAll[tempNode.getNearVertexNumber()] = tempNode.getDistance();
            whichVertexToMe[tempNode.getNearVertexNumber()] = startNumber;
            tempNode = tempNode.getNext();
        }
        haveVisited[startNumber] = true;//起点记录为访问过
        distanceToAll[startNumber] = 0;
        double minDistance = Double.MAX_VALUE; //用于在distanceToAll中确定下一个要访问的顶点
        int nextVertex = startNumber; //用于记录下一个要访问的顶点的下标
        while(nextVertex != endNumber){
            for (int i = 1; i < size; i++) {
                if(!haveVisited[i] && distanceToAll[i] <= minDistance){
                    nextVertex = i;
                    minDistance = distanceToAll[i];
                }
            }///遍历一遍未访问的节点并找到最近的节点。
            //可以对每个顶点作一个标记，更新distanceToAll数组时，把该标记值设为到达该顶点的顶点下标，最后倒序输出
            haveVisited[nextVertex] = true;
            tempNode = vertexes[nextVertex].front;
            while(tempNode != null){
                if( (minDistance + tempNode.getDistance()) < distanceToAll[tempNode.getNearVertexNumber()]) {
                    distanceToAll[tempNode.getNearVertexNumber()] = (minDistance + tempNode.getDistance());
                    whichVertexToMe[tempNode.getNearVertexNumber()] = nextVertex;
                }
                tempNode = tempNode.getNext();
            }//遍历该访问的节点的邻接点，更新distanceToAll数组，从而为查找下一个要访问的顶点做准备
            minDistance = Double.MAX_VALUE; //这个标记记得改为最大值，否则循环不会停止
        }
        /*for (int i = 1; i < size; i++) {
            System.out.println(distanceToAll[i]);
        }*///查看起点到其他点的最短路径，后续可以删除。。。。
        int tempNumber = endNumber;
        while(whichVertexToMe[tempNumber] != startNumber){
            tempNumber = whichVertexToMe[tempNumber];
            Path = String.valueOf(tempNumber) + "->" + Path; //插在原字符串的前面就形成倒序
        }
        Path = String.valueOf(startNumber) + "->" + Path;
        //System.out.println("起点到终点的最短路径长度为" + distanceToAll[endNumber]);
        if(mode == 1)
            return Path;
        else{
            return String.valueOf(distanceToAll[endNumber]);
        }
    }
    @RequestMapping("/moretargets")
    public String getPathWithMoreTargets(int startNumber,int[] targetNumbers){
        String path = "";
        CrossLinkedListVertex[] necessaryVertexes = new CrossLinkedListVertex[targetNumbers.length + 1];
        necessaryVertexes[0] = new CrossLinkedListVertex();
        necessaryVertexes[0].setId(startNumber);
        for (int i = 1; i < necessaryVertexes.length; i++) {
            necessaryVertexes[i] = new CrossLinkedListVertex();
            necessaryVertexes[i].setId(targetNumbers[i-1]);
        }
        CrossLinkedListNode tempOutNode;
        CrossLinkedListNode tempInNode;
        for (int i = 0; i < necessaryVertexes.length; i++) {
            for (int j = 0; ; j++) {
                if(j == i) j++;
                if(j >= necessaryVertexes.length) break;
                tempOutNode = necessaryVertexes[i].firstOut;
                tempInNode = necessaryVertexes[j].firstIn;
                if(tempOutNode == null) {
                    necessaryVertexes[i].firstOut = new CrossLinkedListNode(i, j, Double.parseDouble(getShortedPath(necessaryVertexes[i].getId(), necessaryVertexes[j].getId(), 2)));
                    if(necessaryVertexes[j].firstIn == null)
                        necessaryVertexes[j].firstIn = necessaryVertexes[i].firstOut;
                    else{
                        while(tempInNode.nextSameHeadIn!=null)
                            tempInNode = tempInNode.nextSameHeadIn;
                        tempInNode.nextSameHeadIn = necessaryVertexes[i].firstOut;
                    }
                }
                else{
                    while(tempOutNode.nextSameTailOut!=null)
                        tempOutNode = tempOutNode.nextSameTailOut;
                    tempOutNode.nextSameTailOut= new CrossLinkedListNode(i,j,Double.parseDouble(getShortedPath(necessaryVertexes[i].getId(),necessaryVertexes[j].getId(),2)));
                    if(necessaryVertexes[j].firstIn == null)
                        necessaryVertexes[j].firstIn = tempOutNode.nextSameTailOut;
                    else{
                        while(tempInNode.nextSameHeadIn!=null)
                            tempInNode = tempInNode.nextSameHeadIn;
                        tempInNode.nextSameHeadIn = tempOutNode.nextSameTailOut;
                    }
                }
            }
        } //循环每两点间最短距离来构造一个必经点的十字链表，其中的编号代表的是必经点数组的下标，和序列化文件不同

        //利用贪心算法求上界
        boolean[] visited = new boolean[necessaryVertexes.length];//默认值为false，表示某个点是否已被访问，未访问的值为false
        double upperBound = 0.0;
        double tempDistance = Double.MAX_VALUE;
        int temp = 0;
        for (int i = 0; i < necessaryVertexes.length-1; i++) {
            tempOutNode = necessaryVertexes[temp].firstOut.nextSameTailOut; //不能取firstOut，因为起点要最后回
            while(tempOutNode != null){
                if(!visited[tempOutNode.headVertex] && tempOutNode.getDistance()<tempDistance){
                    tempDistance = tempOutNode.getDistance();
                    temp = tempOutNode.headVertex;
                }
                tempOutNode = tempOutNode.nextSameTailOut;
            }
            upperBound = upperBound + tempDistance;
            visited[temp] = true;
            tempDistance = Double.MAX_VALUE;
        }
        upperBound = upperBound + necessaryVertexes[temp].firstOut.getDistance(); //其他顶点的firstOut一定是到起点的，因为当初是从0开始存的
        System.out.println("上界为"+ upperBound);
        //开始找路径，每次都要选择下界最小的结点进行展开。每次添加活结点时都要计算下界，下界小于上界的不让添加
        int mark = 1;//循环次数的标记
        String necessaryPath = "0"; //表示必经点路径从下标0，也就是起点开始
        String tempPath;
        LinkedList<LinkedListNode> queue = new LinkedList<>();
        tempOutNode = necessaryVertexes[0].firstOut;
        double lowerBound;
        while(tempOutNode!=null){
            for (int j = 1; j < visited.length; j++) {
                visited[j] = false;
            }
            visited[0] = true;//每次计算下界时都要重置下visited数组
            lowerBound = calculateLowerBound(0,necessaryVertexes,visited,0,tempOutNode.headVertex);
            if(lowerBound <=upperBound)
                queue.add(new LinkedListNode(tempOutNode.headVertex,tempOutNode.getDistance(),necessaryPath,lowerBound));
            tempOutNode = tempOutNode.nextSameTailOut;
        } //将下界≤上界的起点的临近点（其实就是所有目标点）存入queue
        double min=0;
        while(true){
            //System.out.println("mark" + (mark++));
            if (mark++ > 512)
                return "计算时间过长，无法得到结果";
            double tempBound = Double.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < queue.size(); i++) {
                if(queue.get(i).lowerBound < tempBound){
                    index = i;
                    tempBound = queue.get(i).lowerBound;
                }
            }//找出队列中下界最小的点
            if(index == -1){
                return "数据使用错误";
            }//应对有时随机数据有误的情况，后续可删
            if (allTargetsGet(targetNumbers.length,queue.get(index).pathFromStartToThis)){
                necessaryPath = queue.get(index).pathFromStartToThis + "->0";
                min = queue.get(index).distanceFromStartToThis + necessaryVertexes[queue.get(index).NodeNumber].firstOut.distance;
                break;
            }//循环的终止条件要用allTargetsGet方法，当最小下界的路径已经包含了全部顶点，那么就算结束
            //在new LinkedListNode前计算下界！这之后再根据下界值决定是否添加结点
            tempPath = queue.get(index).pathFromStartToThis;
            String[] pathArray = tempPath.split("->");//字符分割
            tempOutNode = necessaryVertexes[queue.get(index).NodeNumber].firstOut;
            while(tempOutNode != null){
                for (int j = 0; j < visited.length; j++) {
                    visited[j] = false;
                }
                for (int i = 0; i < pathArray.length; i++) {
                    visited[Integer.parseInt(pathArray[i])] = true;
                } //根据路径重置下visited数组
                lowerBound = calculateLowerBound(queue.get(index).distanceFromStartToThis,necessaryVertexes,visited,queue.get(index).NodeNumber,tempOutNode.headVertex);
                if (lowerBound <= upperBound)
                    queue.add(new LinkedListNode(tempOutNode.headVertex,queue.get(index).distanceFromStartToThis+tempOutNode.distance,tempPath,lowerBound));
                tempOutNode = tempOutNode.nextSameTailOut;
            }
            queue.remove(index);
        }//循环得到最短路径necessaryPath，其中的值是necessaryVertexes数组的下标
        System.out.println("最短路径长度"+min);
        System.out.println("必经点的顺序（下标表示）"+necessaryPath);
        String[] pathArray = necessaryPath.split("->");//字符分割
        int[] necessaryPathVertexNumber = new int[pathArray.length];
        for (int i = 0; i < pathArray.length-1; i++) {
            necessaryPathVertexNumber[i] = necessaryVertexes[Integer.parseInt(pathArray[i])].id;
            path = path.concat(necessaryPathVertexNumber[i] + "->");
        }
        path = path.concat(String.valueOf(necessaryPathVertexNumber[0]));
        System.out.println("必经点的顺序" + path);
        //这里得到的是必经点的最短路径，还需要在此基础上再调用getShortedPath来得到完整的路径
        String realPath = necessaryPathToRealPath(path);
        return realPath;
    }

    public double calculateLowerBound(double distanceFromStartToThis,CrossLinkedListVertex[] necessaryVertexes,boolean[] visited,int lastVertexInPath,int nextVertex){
        //下界的计算就是已走的路径长度+未走入边的点的最小入边+未走出边的点的最小出边
        CrossLinkedListNode tempOutNode;
        CrossLinkedListNode tempInNode;
        if (nextVertex == 0 || visited[nextVertex])
            return Double.MAX_VALUE;  //回到起点必须是最后一步，其他步出现回到起点的情况都必须去掉;已访问的也不行
        double lowerBound = distanceFromStartToThis; //这个distanceFromStartToThis是起点到前一个点的距离，不是到待加入点
        tempOutNode = necessaryVertexes[lastVertexInPath].firstOut;
        while(tempOutNode.headVertex != nextVertex)
            tempOutNode = tempOutNode.nextSameTailOut;
        lowerBound = lowerBound + tempOutNode.distance*2;
        visited[nextVertex] = true;
        double minOutDistance = Double.MAX_VALUE;
        double minInDistance = Double.MAX_VALUE;
        for (int i = 0; i < necessaryVertexes.length; i++) {
            if (visited[i])
                continue; //visited数组对应值为true的点不访问，继续循环
            tempOutNode = necessaryVertexes[i].firstOut;
            while(tempOutNode!=null){
                if(minOutDistance > tempOutNode.distance)
                    minOutDistance = tempOutNode.distance;
                tempOutNode = tempOutNode.nextSameTailOut;
            }
            lowerBound = lowerBound + minOutDistance*2;
            minOutDistance = Double.MAX_VALUE;
        } //循环求得剩下未访问点的最短入边和最短出边,而无向图中这俩就是同一条路，所以不需要再找其最短入边了，提高效率
        int allGet = 0;
        int visitedSum = 0;
        for (int i = 0; i < visited.length; i++) {
           if(visited[i])
               visitedSum++;
        } //查看已访问的点的个数
        if(visitedSum == visited.length-1){
            //如果待加入点是倒数第二个未访问的点，则剩下的边默认就是待加入点到最后一个点的边和最后一个点到起点的边
            int i;
            for (i = 0; i < visited.length; i++) {
                if(!visited[i])
                    break;
            } //找到最后一个未访问的点
            tempOutNode = necessaryVertexes[nextVertex].firstOut;
            while(tempOutNode.headVertex != i)
                tempOutNode = tempOutNode.nextSameTailOut;
            lowerBound = lowerBound + tempOutNode.distance * 2; //待加入点到最后一个点的边
            tempOutNode = necessaryVertexes[i].firstOut;
            lowerBound = lowerBound + tempOutNode.distance*2; //最后一个点到起点的边
        }
        else if(visitedSum == visited.length){
            lowerBound = lowerBound + necessaryVertexes[nextVertex].firstOut.distance*2;
        }//如果待加入点是最后一个未访问的点，则起点的入边和待加入点的出边必须是待加入点到起点的那条边
        else { //如果待加入点不是上述两种情况
            tempInNode = necessaryVertexes[0].firstIn;
            while (tempInNode != null) {
                if (minInDistance > tempInNode.distance)
                    minInDistance = tempInNode.distance;
                tempInNode = tempInNode.nextSameHeadIn;
            }
            lowerBound = lowerBound + minInDistance;
            //再加上起点的最短入边
            tempOutNode = necessaryVertexes[nextVertex].firstOut;
            while (tempOutNode != null) {
                if (minOutDistance > tempOutNode.distance)
                    minOutDistance = tempOutNode.distance;
                tempOutNode = tempOutNode.nextSameTailOut;
            }
            lowerBound = lowerBound + minOutDistance; //再加上待加入点的最短出边
        }
        return lowerBound/2;
    }

    public boolean allTargetsGet(int targets,String Path){
        String[] PathArrays = Path.split("->");
        for (int i = 1; i <= targets; i++) {
            int j;
            for (j = 1; j < PathArrays.length; j++) {
                if(PathArrays[j].equals(String.valueOf(i)))
                    break;
            }
            if(j == PathArrays.length)
                return false;
        }
        return true;
    } //用于多目标最短路径，确认每个要到的目标都达到了;因为起点下标为0，其他目标点下标均为1-n，所以只要判断必经点路径是否包含了所有1-n的数即可

    class LinkedListNode {
        int NodeNumber;
        double distanceFromStartToThis;//从起点到该点的路径的距离
        String pathFromStartToThis;
        double lowerBound;
        public LinkedListNode(int nodeNumber, double distanceFromStartToThis, String pathFromStartToThis,double lowerBound) {
            NodeNumber = nodeNumber;
            this.distanceFromStartToThis = distanceFromStartToThis;
            this.pathFromStartToThis = pathFromStartToThis + "->" +nodeNumber;
            this.lowerBound = lowerBound;
        }
    }

    public String getPathWithMoreTargetsUsingARA(int startNumber,int[] targetNumbers){
        String path = "";
        ARAVertex[] araVertices = new ARAVertex[targetNumbers.length +1]; //ARA算法使用的顶点也都是必经点
        araVertices[0] = new ARAVertex(startNumber);
        for (int i = 1; i < araVertices.length; i++) {
            araVertices[i] = new ARAVertex(targetNumbers[i-1]);
        }
        ARALinkedListNode tempNode;
        for (int i = 0; i < araVertices.length; i++) {
            for (int j = 0; ; j++) {
                if(j == i) j++;
                if(j >= araVertices.length) break;
                if(araVertices[i].front == null) {
                    araVertices[i].front = new ARALinkedListNode(j, Double.parseDouble(getShortedPath(araVertices[i].id, araVertices[j].id, 2)));
                    araVertices[i].sum = araVertices[i].sum + araVertices[i].front.distance;
                }
                else{
                    tempNode = araVertices[i].front;
                    while(tempNode.next!= null)
                        tempNode = tempNode.next;
                    tempNode.next = new ARALinkedListNode(j,Double.parseDouble(getShortedPath(araVertices[i].id,araVertices[j].id,2)));
                    araVertices[i].sum = araVertices[i].sum + tempNode.next.distance;
                }
            }
        } //创建邻接表
        for (int i = 0; i < araVertices.length; i++) {
            tempNode = araVertices[i].front;
            while(tempNode!=null){
                tempNode.pheromone = tempNode.distance / araVertices[i].sum;
                tempNode = tempNode.next;
            }
        } //初始化每条路径的信息素浓度概率，值为 该路径长度 / 所属顶点全部路径长度和 。 这个初始化可能需要改进？？？？


        double min = Double.MAX_VALUE;
        double min2Sum = 0;
        int antNumbers = 50;
        int[][] allPath = new int[antNumbers][araVertices.length];
        int[] bestPath = new int[araVertices.length];
        for (int examTimes = 0; examTimes < 30; examTimes++) {
            double min2 = Double.MAX_VALUE;
            for (int i = 0; i < 400; i++) {
                double[] pathLength = new double[antNumbers]; //每只蚂蚁走的路径的总长
                for (int j = 0; j < antNumbers; j++) {
                    allPath[j] = antFindPath(araVertices, pathLength, j);
                    if (pathLength[j] < min) {
                        bestPath = allPath[j];
                        min = pathLength[j];
                    }
                    if(pathLength[j] < min2)
                        min2 = pathLength[j];  //统计表格所需，后面可删除
                }//50只蚂蚁从起点邻近的目标点随机找寻路径
                //根据找寻完的路径释放信息素（之所以不把释放信息素顺便放在找寻路径里面是因为防止前面的蚂蚁对后面的蚂蚁造成影响）
                for (int j = 0; j < antNumbers; j++) {
                    tempNode = araVertices[0].front;//allPath[][0]是起点出发到达的第一个目标点
                    for (int k = 0; k < araVertices.length; k++) {
                        while (tempNode.id != allPath[j][k])
                            tempNode = tempNode.next;
                        tempNode.pheromone = tempNode.pheromone + 0.01 / pathLength[j];//路径得到信息素
                        tempNode.pheromone = tempNode.pheromone * 0.75; //挥发信息素，0.8为挥发因子
                        tempNode = araVertices[allPath[j][k]].front;
                    }
                } //释放并挥发信息素，路径中每条边得到的信息素就是 100/路径总长 ， 分子为100是因为路径总长太长了，分子不大点的话信息素浓度太小不知道会不会有影响？？？？？
                //用邻接表的话，释放信息素的时候需要套三层循环，效率不够的话用矩阵会不会快点？？？？？？
            }//迭代400次找寻路径
            min2Sum = min2Sum + min2;
        }//实验30次并根据30*400次找寻路径的结果输出最短路径

        String bestPathString = "0->";
        path = araVertices[0].id + "->";
        for (int i = 0; i < bestPath.length-1; i++) {
            bestPathString = bestPathString.concat(bestPath[i] + "->");
            path = path.concat(araVertices[bestPath[i]].id + "->");
        }
        bestPathString = bestPathString.concat(String.valueOf(bestPath[bestPath.length-1]) );

        path = path.concat(String.valueOf(araVertices[bestPath[bestPath.length-1]].id) );
        System.out.println("30次实验下最短路径长度为"+ min);
        System.out.println("最短路径平均值"+ min2Sum / 30);
        System.out.println("必经点的顺序（下标表示）"+bestPathString);
        System.out.println("必经点的顺序为" + path);
        String realPath = necessaryPathToRealPath(path);
        return realPath;
    }


    public int[] antFindPath(ARAVertex[] araVertices,double[] pathLength,int antNumber){
        //蚂蚁根据公式不断找寻一段路径
        int[] path = new int[araVertices.length];
        for (int i = 0; i < path.length; i++) {
            path[i] = -1;
        } //防止默认值0影响到isInPath的判断
        int random = (int)(Math.random()*araVertices.length);//选一个随机点，增加随机性。此处默认了起点，即path[0]是第一个走的点
        while(random == 0)
            random = (int)(Math.random()*araVertices.length); //path[0]不能是起点，即random一开始不能为0
        int current = random;
        path[0] = random;
        ARALinkedListNode tempNode = araVertices[0].front;
        while(tempNode.id != random)
            tempNode = tempNode.next;
        pathLength[antNumber] = pathLength[antNumber] + tempNode.distance;
        for (int i = 1; i < araVertices.length; i++) {
            int next = roulette(araVertices,current,path,pathLength,antNumber);
            path[i] = next;
            current = next;
        }//使用轮盘赌+蚁群算法公式得到路径
        return path;
    }
    public int roulette(ARAVertex[] araVertices,int currentVertex,int[] path,double[] pathLength,int antNumber) {
        double probability = 0;
        double molecule = 0; //公式的分子
        double denominator = 0; //公式的分母
        int alpha = 1; //公式的α
        int beta = 2; //公式的β
        int random = 0;
        ARALinkedListNode tempNode = araVertices[currentVertex].front;
        while (tempNode != null) {
            if (isInPath(tempNode.id, path)) {
                tempNode = tempNode.next;
                continue; //公式需要的是未达到的顶点，所以已在路径中的不计算到公式中
            }
            denominator = denominator + Math.pow(tempNode.pheromone, alpha) + Math.pow(1.0 / tempNode.distance, beta);
            tempNode = tempNode.next;
        }
        while(probability < 0.3) {
            //该循环使用了轮盘赌思想，当超过区间0.3时说明概率已经很大了，取该点继续前进。
            random = (int)(Math.random()*araVertices.length); //随机取一个还未走的点
            if(random == 0 && path[araVertices.length-2] == -1)
                continue; //起点必须是终点，所以0值不能出现除path[]最后一个的其他地方
            if(path[araVertices.length-2] != -1)
                random = 0; //最后一个点指定为0，即回到起点
            if (isInPath(random,path))
                continue;
            tempNode = araVertices[currentVertex].front;
            while(tempNode.id != random)
                tempNode = tempNode.next; //找到当前点和随机取的点之间的路径
            molecule = Math.pow(tempNode.pheromone,alpha) + Math.pow(1.0 / tempNode.distance, beta);
            probability = probability + molecule/denominator;
        }
        pathLength[antNumber] = pathLength[antNumber] + tempNode.distance;
        return random;
    }
    public boolean isInPath(int x , int[] path){
        for (int i = 0; i < path.length; i++) {
            if (x == path[i])
                return true;
        }
        return false;
    }

    public String necessaryPathToRealPath(String necessaryPath){
        String path = "";
        //由必经点路径得到实际路径，其实就是多次调用getShortedPath并整理
        String[] strings = necessaryPath.split("->");
        int[] necessaryVertexes = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            necessaryVertexes[i] = Integer.parseInt(strings[i]);
        }
        path = getShortedPath(necessaryVertexes[0],necessaryVertexes[1],1);
        for (int i = 1; i < strings.length-1; i++) {
            String temp = getShortedPath(necessaryVertexes[i],necessaryVertexes[i+1],1);
            int tempIndex = temp.indexOf("->");
            path = path.concat(temp.substring(tempIndex));
        }
        return path;
    }
}

class Vertex implements Serializable{
    private int id;
    public Node front = null;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}

class Node implements Serializable{
    private Node next = null;
    private int nearVertexNumber;
    private double distance;
    private int type;
    //private boolean visited = true; 记录该边是否已被访问，true表示该边可以走，但没用到
    public Node() {}
    public Node(int nearVertexNumber, double distance, int type) {
        this.nearVertexNumber = nearVertexNumber;
        this.distance = distance;
        this.type = type;
    }
    public Node getNext() {
        return next;
    }
    public void setNext(Node next) {
        this.next = next;
    }
    public int getNearVertexNumber() {
        return nearVertexNumber;
    }
    public void setNearVertexNumber(int nearVertexNumber) {
        this.nearVertexNumber = nearVertexNumber;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    /*public boolean getVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }*/
}

class CrossLinkedListVertex{
    public int id;
    public CrossLinkedListNode firstIn = null; //指向第一个入边
    public CrossLinkedListNode firstOut = null; //指向第一个出边
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    //加点getter、setter？？？
}

class CrossLinkedListNode{
    public int tailVertex; //弧尾，即没有箭头的一边
    public int headVertex; //弧头，即有箭头的一边
    public CrossLinkedListNode nextSameHeadIn = null;
    public CrossLinkedListNode nextSameTailOut = null;
    public double distance;
    //public boolean visited = false; 记录该边是否被访问过，但好像用不上
    //加点getter、setter？？？
    public CrossLinkedListNode(int tailVertex, int headVertex, double distance) {
        this.tailVertex = tailVertex;
        this.headVertex = headVertex;
        this.distance = distance;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    /*public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }*/
}

class ARAVertex{
    public int id;
    public ARALinkedListNode front = null;
    public double sum = 0; //该顶点的邻接路径长度之和
    public ARAVertex(int id) {
        this.id = id;
    }
}

class ARALinkedListNode{
    ARALinkedListNode next = null;
    public int id; //对应必经点数组下标，不同于整个地图
    public double distance;
    public double pheromone; //信息素浓度概率

    public ARALinkedListNode(int id, double distance) {
        this.id = id;
        this.distance = distance;
    }
}

class Road implements Serializable {
    //用于json
    private int Eid;
    private int From;
    private int To;
    private double Length;
    private int Type;

    public int getEid() {
        return Eid;
    }
    public void setEid(int eid) {
        Eid = eid;
    }
    public int getFrom() {
        return From;
    }
    public void setFrom(int from) {
        From = from;
    }
    public int getTo() {
        return To;
    }
    public void setTo(int to) {
        To = to;
    }
    public double getLength() {
        return Length;
    }
    public void setLength(double length) {
        Length = length;
    }
    public int getType() {
        return Type;
    }
    public void setType(int type) {
        Type = type;
    }
}
