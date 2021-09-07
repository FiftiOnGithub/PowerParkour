package me.anon.main;
@SuppressWarnings("unused")
/*
CM - Completion Messages
Sent when a player completes a level. Not determined whether to only include gold times or pbs, or whether all should be allowed.

PF - PreFixes
Prefixes in chat. The only tab prefix is P.

JM - Join Messages
Messages sent when players join or leave.

MDT - MiDas Touch
Earn more money. 10% more per level.

DC_EXTRA - Extra lives in Daily Challenge
+1 life. Only 1 level.


Undecided as of yet how tiered items will work. Selector is ineffective.

*/
public enum ShopCosmetic {
    CM_NONE,CM_BASIC,CM_FLEX,PF_NONE,PF_PLUS,PF_PRO,PF_SMALLSTAR,PF_BIGSTAR,PF_STAFF,PF_CHRISTMAS_TREE,JM_NONE,JM_BASIC,JM_RAINBOW,MDT_NONE,MDT_ONE,MDT_TWO,DC_EXTRA_ONE
}
