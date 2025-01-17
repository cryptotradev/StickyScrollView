package com.amar.library.ui.presentation;

/**
 * Created by Amar Jain on 17/03/17.
 */

public interface IStickyScrollPresentation {
    void freeTopHeader();
    void freeHeader();
    void freeFooter();
    void stickHeader(int translationY);
    void stickTopHeader(int translationY);
    void stickFooter(int translationY);

    void initHeaderView(int headerTopId, int headerId, int headerContainerId);
    void initFooterView(int id);

    int getCurrentScrollYPos();
}
