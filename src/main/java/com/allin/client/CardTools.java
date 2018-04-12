package com.allin.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CardTools {

    //随机指定数量的扑克
    public static List<Integer> getCards(final int number) {


        final Random r = new Random();
        final List<Integer> list = new ArrayList<>();
        int i;
        while (list.size() < number) {
            i = r.nextInt(52);
            if (!list.contains(i)) {
                list.add(i);
            }
        }

        return list;
    }

    //比较大小
    public static List<CardPlayer> compareBigNumber(List<CardPlayer> peopleList) {

        //获取牌类型
        peopleList = getCardStyle(peopleList);
        //5.豹子+9000

        //4.顺金+8000

        //3.金花+7000 都是金花 找最大张单牌

        //2.顺子+6000

        //1.对子+5000

        //0.单牌+0

        return peopleList;
    }


    //获取牌类型
    public static List<CardPlayer> getCardStyle(final List<CardPlayer> peopleList) {
        //先判断cardStyle
        peopleList.stream().forEach(p -> {
            final int one = p.getFirstCard();
            final int two = p.getSecondCard();
            final int three = p.getThirdCard();
            final int oneTen = one / 10;
            final int twoTen = two / 10;
            final int threeTen = three / 10;
            final int fightingCapacity = oneTen + twoTen + threeTen;
            //初始化战斗力
            p.setFightingCapacity(fightingCapacity);


            //判断对子
            if (oneTen == twoTen || oneTen == threeTen || twoTen == threeTen) {
                p.setCardStyle(1);
                p.setFightingCapacity(p.getFightingCapacity() + 5000);
                if (oneTen == twoTen) {
                    p.setFightingCapacity(p.getFightingCapacity() + oneTen * 14);
                } else if (oneTen == threeTen) {
                    p.setFightingCapacity(p.getFightingCapacity() + oneTen * 14);
                } else if (twoTen == threeTen) {
                    p.setFightingCapacity(p.getFightingCapacity() + twoTen * 14);
                }

            }


            final int[] aCard = {oneTen, twoTen, threeTen};
            Arrays.sort(aCard);


            //是否单牌
            if (p.getCardStyle() == 0) {
                if (aCard[0] == p.getFirstCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[0] * 1);
                }
                if (aCard[0] == p.getSecondCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[0] * 1);
                }
                if (aCard[0] == p.getThirdCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[0] * 1);
                }
                if (aCard[1] == p.getFirstCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[1] * 14);
                }
                if (aCard[1] == p.getSecondCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[1] * 14);
                }
                if (aCard[1] == p.getThirdCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[1] * 14);
                }
                if (aCard[2] == p.getFirstCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[2] * 196);
                }
                if (aCard[2] == p.getSecondCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[2] * 196);
                }
                if (aCard[2] == p.getThirdCard() / 10) {
                    p.setFightingCapacity(p.getFightingCapacity() + aCard[2] * 196);
                }
            }


            //判断顺子
            if (aCard[0] + 1 == aCard[1] && aCard[1] + 1 == aCard[2]) {
                p.setCardStyle(2);
                p.setFightingCapacity(p.getFightingCapacity() + 6000);
            }

            //是否金花
            final int oneColor = one - one / 10 * 10;
            final int twoColor = two - two / 10 * 10;
            final int threeColor = three - three / 10 * 10;
            System.out.println(p.getUserId() + ":" + oneColor + "-" + twoColor + "-" + threeColor);
            if (oneColor == twoColor && oneColor == threeColor) {
                //是否顺金
                if (p.getCardStyle() == 2) {
                    p.setCardStyle(4);
                    p.setFightingCapacity(p.getFightingCapacity() + 8000);
                } else {
                    p.setCardStyle(3);
                    p.setFightingCapacity(p.getFightingCapacity() + 7000);
                }
            }

            //是否豹子
            if (oneTen == twoTen && twoTen == threeTen) {
                p.setCardStyle(5);
                p.setFightingCapacity(p.getFightingCapacity() + 9000);
            }



        });


        return peopleList;

    }

    public static void main(final String[] args) {

        final int threeTen = 129 / 10;
        System.out.println(threeTen);

//        final int[] list = {4, 2, 6, 7, 2, 9, 0};
//
//        Arrays.sort(list);
//        Arrays.stream(list).forEach(a -> System.out.println(a));
//        System.err.println("简化版JDK1.8新特性==" + list);
    }


}
