/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.heuristic.selector.move.composite;

import java.util.List;
import java.util.Random;

import org.drools.planner.core.heuristic.selector.move.AbstractMoveSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;

/**
 * Abstract superclass for every composite {@link MoveSelector}.
 * @see MoveSelector
 */
public abstract class CompositeMoveSelector extends AbstractMoveSelector {

    protected final List<MoveSelector> childMoveSelectorList;
    protected boolean randomSelection = false;

    protected Random workingRandom = null;

    protected CompositeMoveSelector(List<MoveSelector> childMoveSelectorList) {
        this.childMoveSelectorList = childMoveSelectorList;
        for (MoveSelector childMoveSelector : childMoveSelectorList) {
            solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        }
    }

    public boolean isRandomSelection() {
        return randomSelection;
    }

    public void setRandomSelection(boolean randomSelection) {
        this.randomSelection = randomSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        validate();
        workingRandom = solverPhaseScope.getWorkingRandom();
    }

    protected void validate() {
        if (!randomSelection && !childMoveSelectorList.isEmpty()) {
            // Only the last childMoveSelector can be neverEnding
            for (MoveSelector childMoveSelector : childMoveSelectorList.subList(0, childMoveSelectorList.size() - 1)) {
                if (childMoveSelector.isNeverEnding()) {
                    throw new IllegalStateException("The non-last childMoveSelector (" + childMoveSelector
                            + ") has neverEnding (" + childMoveSelector.isNeverEnding()
                            + ") on a class (" + getClass().getName()  + ") instance with randomSelection ("
                            + randomSelection + ").");
                }
            }
        }
    }

    public boolean isContinuous() {
        for (MoveSelector moveSelector : childMoveSelectorList) {
            if (moveSelector.isContinuous()) {
                return true;
            }
        }
        return false;
    }

}
