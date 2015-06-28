package com.m303.roomba;

import com.m303.roomba.nav.Routing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Created by mley on 24.04.15.
 */
public class MappingBrain implements Brain {


    private Field field = new Field();
    private int carriedButton = -1;
    private int nextButton = 0;
    private Deque<String> commands = new ArrayDeque<>();

    public MappingBrain() {
        commands.add(PUSH);
        commands.add(LOOK);
        commands.add(RIGHT);
        commands.add(LOOK);
        commands.add(RIGHT);
        commands.add(LOOK);
        commands.add(RIGHT);
        commands.add(LOOK);
    }

    @Override
    public String think(String action, Map<String, Object> result) {
        switch (action) {
            case START:
                break;

            case LEFT:
                field.headLeft();
                break;
            case RIGHT:
                field.headRight();
                break;
            case WALK:
                field.walk();
                checkButton();
                break;
            case SWAP:
                swappedButton((String) result.get("button"));
                break;
            case PUSH:
                checkPush((String) result.get("button"));
                break;
            case LOOK:
                field.discover(result);
                break;
        }

        return next();
    }

    private void checkPush(String button) {
        if (" ".equals(button)) {
            field.getCurrent().setButtonState(Cell.State.Free);
            field.getCurrent().setButton(-1);
        } else {
            int b = Integer.parseInt(button);
            field.getCurrent().setButton(b);
            field.getCurrent().setButtonState(Cell.State.Occupied);
            if (b == nextButton) {
                nextButton++;
            }
        }

    }

    private void swappedButton(String button) {
        boolean dropped = false;
        if (carriedButton != -1) {
            // if we had a button, we dropped it
            field.getCurrent().setButton(carriedButton);
            field.getCurrent().setButtonState(Cell.State.Occupied);
            carriedButton = -1;
            dropped = true;
        }
        if (" ".equals(button)) {
            // there was no button
            if(!dropped) {
                field.getCurrent().setButtonState(Cell.State.Free);
            }
        } else {
            // we found a button
            carriedButton = Integer.parseInt(button);
            if(!dropped) {
                field.getCurrent().setButton(-1);
                field.getCurrent().setButtonState(Cell.State.Free);
            }
        }
    }


    private void checkButton() {
        int button = -1;
        try {
            button = field.getCurrent().getButton();
        } catch(Exception e) {
            e.printStackTrace();
        }
        // there is a button on the current field
        if (button >= 0) {
            if (button == nextButton) {
                commands.addFirst(PUSH);
                return;
            }
            if (carriedButton == -1) {
                // take button, if we have none
                commands.addFirst(SWAP);
                return;
            } else {
                if (carriedButton > button) {
                    // if button on field is smaller than carried button, then swap
                    commands.addFirst(SWAP);
                    return;
                }
            }
        } else {
            // no button, but we're carrying the next button
            if (carriedButton == nextButton) {
                commands.clear();
                commands.addFirst(PUSH);
                commands.addFirst(SWAP);
            }
        }
    }

    private String next() {
        if (commands.isEmpty()) {
            plan();
        }


        return commands.remove();
    }

    private void plan() {
        System.out.println(field.toString());
        Cell c = null;

        // check if we are carrying the next button to press, go to next free field, drop, press
        if(carriedButton != -1 && carriedButton == nextButton) {
            c = field.getRouting().bfSearch(new Routing.Selector() {
                @Override
                public boolean select(Cell c) {
                    return c.isFree() && !c.hasButton();
                }
            });
            goTo(c, field.getHeading(), true);
            return;
        }


        c = field.findButton(nextButton);
        if (c != null) {
            // if we know the next button, we go there
            goTo(c, field.getHeading(), true);
        } else {

            long a = System.currentTimeMillis();
            c = field.findNearestUnknownButtonG();
            System.out.println("finding next field took: " + (System.currentTimeMillis() - a));



            goTo(c, Direction.NORTH, true);

            // do not actually go on the field
            commands.removeLast();

            // look at it, Hector!
            commands.addLast(LOOK);

        }
    }



    private void goTo(Cell c, Direction dir, boolean ignoreDestDir) {
        long a = System.currentTimeMillis();

        commands.addAll(field.navigate(c, dir, ignoreDestDir));
        System.out.println("calculating route took: " + (System.currentTimeMillis() - a));



    }


}
