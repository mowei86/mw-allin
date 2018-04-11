package com.allin.client;

import java.util.ArrayList;
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
        //5.豹子+1500

        //4.顺金+1200

        //3.金花+900 都是金花 找最大张单牌

        //2.顺子+600

        //1.对子+300

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
            final int oneTen = one / 10 % 10;
            final int twoTen = two / 10 % 10;
            final int threeTen = three / 10 % 10;


            //判断对子
            if (oneTen == twoTen || oneTen == threeTen || twoTen == threeTen) {
                p.setCardStyle(1);
            }

            //判断顺子


            //是否金花
            final int oneColor = one - one / 10 * 10;
            final int twoColor = two - two / 10 * 10;
            final int threeColor = three - three / 10 * 10;
            System.out.println(p.getUserId() + ":" + oneColor + "-" + twoColor + "-" + threeColor);
            if (oneColor == twoColor && oneColor == threeColor) {
                p.setCardStyle(3);
            }
        });


        return peopleList;

    }
}
