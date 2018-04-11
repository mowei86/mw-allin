package com.allin.client;


import lombok.AllArgsConstructor;
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
    private int fourthCard;
    private int fifthCard;


}
