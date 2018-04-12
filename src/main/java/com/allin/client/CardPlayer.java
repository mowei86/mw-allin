package com.allin.client;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPlayer {

    private int userId;
    private int tableId;
    private int firstCard;
    private int secondCard;
    private int thirdCard;

    //战斗力
    private int fightingCapacity = 0;

    //豹子5//顺金4//金花3//顺子2//对子1//单牌0
    @Builder.Default
    private int cardStyle = 0;



}
