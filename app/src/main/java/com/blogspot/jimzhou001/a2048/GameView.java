package com.blogspot.jimzhou001.a2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

//4×4方格
public class GameView extends GridLayout {

    public static Card[][] cards = new Card[4][4];//4×4=16张卡片
    private static List<Point> emptyPoints = new ArrayList<Point>();//空卡片（数值为0）位置
    public int num[][] = new int[4][4];//用于后退一步
    public int score;//用于后退一步
    public boolean hasTouched = false;

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    //初始化游戏布局
    private void initGameView() {
        setRowCount(4);
        setColumnCount(4);
        setOnTouchListener(new Listener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        int cardWidth = (Math.min(w, h)-10)/4;//根据屏幕大小设置卡片宽度
        addCards(cardWidth, cardWidth);
        startGame();

    }

    //初始化卡片
    private void addCards(int cardWidth, int cardHeight) {
        this.removeAllViews();
        Card c;
        for(int y=0;y<4;++y) {
            for(int x = 0;x<4;++x) {
                c = new Card(getContext());
                c.setNum(0);
                addView(c, cardWidth, cardHeight);
                cards[x][y] = c;
            }
        }
    }

    //随即增加卡片，数值为2或4（二者概率不同）
    private static void addRandomNum() {
        emptyPoints.clear();//重新记录空卡片位置
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                if (cards[x][y].getNum() == 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }
        //随机将一个空卡片的数值设置成2或4（概率之比为9：1）
        Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
        cards[p.x][p.y].setNum(Math.random()>0.1?2:4);
    }

    //开始游戏
    public static void startGame() {
        MainActivity.getMainActivity().clearScore();
        for(int y=0;y<4;++y) {
            for(int x=0;x<4;++x) {
                cards[x][y].setNum(0);
            }
        }
        addRandomNum();
        addRandomNum();
    }

    //向左滑动
    private void swipeLeft() {
        boolean b = false;
        //每一行（横坐标为x，纵坐标为y）
        for(int y=0;y<4;++y) {
            //每一列（考虑到最后一列无需继续比较，故此处只需3列）
            for(int x=0;x<3;++x) {
                //比较卡片数值
                for(int x1=x+1;x1<4;++x1) {
                    //卡片（x1，y）不空，则与（x，y）比较
                    if (cards[x1][y].getNum()>0) {
                        //卡片（x，y）为空，则将（x1，y）左移
                        if (cards[x][y].getNum()==0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            --x;//（x1，y）需要继续比较
                            b = true;
                        } else if (cards[x][y].equals(cards[x1][y])) {
                            //合并卡片
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        //遇到非空卡片，则（x，y）无需继续比较
                        break;
                    }
                }
            }
        }
        //一旦卡片有所变动，便随机添加数值为2或4的卡片，以继续游戏
        if (b) {
            addRandomNum();
            checkGameOver();//每次添加数值为2或4的卡片，都需要判断游戏是否结束
        }
    }

    //向右滑动
    private void swipeRight() {
        boolean b = false;
        for(int y=0;y<4;++y) {
            for(int x=3;x>0;--x) {
                for(int x1=x-1;x1>=0;--x1) {
                    if (cards[x1][y].getNum()>0) {
                        if (cards[x][y].getNum()==0) {
                            cards[x][y].setNum(cards[x1][y].getNum());
                            cards[x1][y].setNum(0);
                            ++x;
                            b = true;
                        } else if (cards[x][y].equals(cards[x1][y])) {
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
        }
    }

    //向上滑动
    private void swipeUp() {
        boolean b = false;
        for(int x=0;x<4;++x) {
            for(int y=0;y<3;++y) {
                for(int y1=y+1;y1<4;++y1) {
                    if (cards[x][y1].getNum()>0) {
                        if (cards[x][y].getNum()==0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            --y;
                            b = true;
                        } else if (cards[x][y].equals(cards[x][y1])) {
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
        }
    }

    //向下滑动
    private void swipeDown() {
        boolean b = false;
        for(int x=0;x<4;++x) {
            for(int y=3;y>0;--y) {
                for(int y1=y-1;y1>=0;--y1) {
                    if (cards[x][y1].getNum()>0) {
                        if (cards[x][y].getNum()==0) {
                            cards[x][y].setNum(cards[x][y1].getNum());
                            cards[x][y1].setNum(0);
                            ++y;
                            b = true;
                        } else if (cards[x][y].equals(cards[x][y1])) {
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cards[x][y].getNum());
                            b = true;
                        }
                        break;
                    }
                }
            }
        }
        if (b) {
            addRandomNum();
            checkGameOver();
        }
    }

    private void checkGameOver() {
        boolean isOver = true;
        ALL:
        for(int y=0;y<4;++y) {
            for(int x=0;x<4;++x) {
                /*游戏继续的条件是：1.至少有一个空卡片
                 *                  2.没有空卡片，但存在相邻两个卡片数值相等，表现为左右相等或上下相等
                 */
                if (cards[x][y].getNum()==0||
                        (x<3&&cards[x][y].getNum()==cards[x+1][y].getNum())||
                        (y<3&&cards[x][y].getNum()==cards[x][y+1].getNum())) {
                    //没有结束，游戏继续
                    isOver = false;
                    break ALL;
                }
            }
        }
        //游戏结束
        if (isOver) {
            new AlertDialog.Builder(getContext()).setTitle("很遗憾，游戏结束啦").setMessage("当前得分为"+MainActivity.score+"，再接再厉哦！").setPositiveButton("点击此处再玩一局", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGame();
                }
            }).show();
        }
    }

    //游戏方格部分滑动监听
    class Listener implements View.OnTouchListener {

        private float startX, startY, offsetX, offsetY;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (!hasTouched) {
                hasTouched = true;
            }

            score = MainActivity.score;

            for(int y=0;y<4;++y) {
                for(int x=0;x<4;++x) {
                    num[y][x] = cards[y][x].getNum();
                }
            }

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    offsetX = motionEvent.getX()-startX;
                    offsetY = motionEvent.getY()-startY;

                    if (Math.abs(offsetX)>Math.abs(offsetY)) {
                        if (offsetX<-5) {
                            swipeLeft();
                        } else if (offsetX>5) {
                            swipeRight();
                        }
                    } else {
                        if (offsetY<-5) {
                            swipeUp();
                        } else if (offsetY>5) {
                            swipeDown();
                        }
                    }

            }

            return true;

        }

    }

}