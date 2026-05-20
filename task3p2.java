import java.util.*;

public class task3p2 {
    static int W,H,N,EX,EY;
    static final int INF = 1 << 28;
    static boolean[][] wall;
    static long[][] itemAt;
    static P[] ps;
    static String[] names;
    static Map<String,Integer> id = new HashMap<>();

    static class P {
        int x,y; String name; char d;
        P(int x,int y,String name,char d){
            this.x=x; this.y=y; this.name=name; this.d=d;
        }
    }

    static class R {
        int dist;
        long seen;
        R(int dist,long seen){
            this.dist=dist; this.seen=seen;
        }
    }

    static int[] pos(P p){
        if(p.d=='E') return new int[]{p.x+1,p.y};
        if(p.d=='W') return new int[]{p.x-1,p.y};
        if(p.d=='N') return new int[]{p.x,p.y-1};
        return new int[]{p.x,p.y+1};
    }

    static boolean inside(int x,int y){
        return 0<=x && x<W && 0<=y && y<H;
    }

    static boolean better(long a,long b){
        int ca = Long.bitCount(a);
        int cb = Long.bitCount(b);
        if(ca != cb) return ca > cb;
        return listString(a).compareTo(listString(b)) < 0;
    }

    static String listString(long mask){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<N;i++){
            if((mask & (1L<<i)) != 0) list.add(names[i]);
        }
        Collections.sort(list);
        return String.join("", list);
    }

    static R bfs(int sx,int sy,int gx,int gy){
        if(!inside(sx,sy) || !inside(gx,gy)) return new R(INF,0);
        if(wall[sy][sx] || wall[gy][gx]) return new R(INF,0);

        int[][] dist = new int[H][W];
        long[][] seen = new long[H][W];

        for(int[] row:dist) Arrays.fill(row,-1);

        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx,sy});
        dist[sy][sx]=0;
        seen[sy][sx]=itemAt[sy][sx];

        int[] dx={1,-1,0,0};
        int[] dy={0,0,1,-1};

        while(!q.isEmpty()){
            int[] c=q.poll();
            int x=c[0], y=c[1];

            for(int k=0;k<4;k++){
                int nx=x+dx[k], ny=y+dy[k];
                if(!inside(nx,ny)) continue;
                if(wall[ny][nx]) continue;

                int nd = dist[y][x] + 1;
                long ns = seen[y][x] | itemAt[ny][nx];

                if(dist[ny][nx] == -1){
                    dist[ny][nx] = nd;
                    seen[ny][nx] = ns;
                    q.add(new int[]{nx,ny});
                }else if(nd == dist[ny][nx] && better(ns, seen[ny][nx])){
                    seen[ny][nx] = ns;
                    q.add(new int[]{nx,ny});
                }
            }
        }

        if(dist[gy][gx] == -1) return new R(INF,0);
        return new R(dist[gy][gx], seen[gy][gx]);
    }

    static void printAns(int dist,long mask){
        ArrayList<String> list = new ArrayList<>();

        for(int i=0;i<N;i++){
            if((mask & (1L<<i)) != 0) list.add(names[i]);
        }

        Collections.sort(list);

        System.out.print(dist);
        for(String s:list){
            System.out.print(" " + s);
        }
        System.out.println();
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        W=sc.nextInt();
        H=sc.nextInt();
        N=sc.nextInt();

        EX=W-1;
        EY=H-1;

        wall = new boolean[H][W];
        itemAt = new long[H][W];
        ps = new P[N];
        names = new String[N];

        for(int i=0;i<N;i++){
            int x=sc.nextInt();
            int y=sc.nextInt();
            String name=sc.next();
            char d=sc.next().charAt(0);

            ps[i]=new P(x,y,name,d);
            names[i]=name;
            id.put(name,i);
            wall[y][x]=true;
        }

        for(int i=0;i<N;i++){
            int[] p = pos(ps[i]);
            if(inside(p[0],p[1])){
                itemAt[p[1]][p[0]] |= 1L << i;
            }
        }

        int Q=sc.nextInt();

        while(Q-- > 0)
            {
            int M=sc.nextInt();
            int[] item = new int[M];

            for(int i=0;i<M;i++){
                item[i]=id.get(sc.next());
            }

            int size = 1 << M;
            int[][] dp = new int[size][M];
            long[][] seen = new long[size][M];

            for(int[] row:dp) Arrays.fill(row,INF);

            for(int i=0;i<M;i++){
                int[] p = pos(ps[item[i]]);
                R r = bfs(0,0,p[0],p[1]);
                dp[1<<i][i] = r.dist;
                seen[1<<i][i] = r.seen;
            }

            for(int mask=0;mask<size;mask++){
                for(int last=0;last<M;last++){
                    if(dp[mask][last] >= INF) continue;

                    int[] from = pos(ps[item[last]]);

                    for(int next=0;next<M;next++){
                        if((mask & (1<<next)) != 0) continue;

                        int[] to = pos(ps[item[next]]);
                        R r = bfs(from[0],from[1],to[0],to[1]);

                        int nmask = mask | (1<<next);
                        int nd = dp[mask][last] + r.dist;
                        long ns = seen[mask][last] | r.seen;

                        if(nd < dp[nmask][next]){
                            dp[nmask][next] = nd;
                            seen[nmask][next] = ns;
                        }else if(nd == dp[nmask][next] && better(ns, seen[nmask][next])){
                            seen[nmask][next] = ns;
                        }
                    }
                }
            }

            int full = size - 1;
            int bestDist = INF;
            long bestSeen = 0;

            for(int last=0;last<M;last++){
                int[] p = pos(ps[item[last]]);
                R r = bfs(p[0],p[1],EX,EY);

                int total = dp[full][last] + r.dist;
                long allSeen = seen[full][last] | r.seen;

                if(total < bestDist){
                    bestDist = total;
                    bestSeen = allSeen;
                }else if(total == bestDist && better(allSeen,bestSeen)){
                    bestSeen = allSeen;
                }
            }

            printAns(bestDist,bestSeen);
        }

        sc.close();
    }
}