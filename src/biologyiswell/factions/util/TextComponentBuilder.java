/*
 * Copyright (C) 2017 rtf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biologyiswell.factions.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentBuilder {

    /** Main component, this represents the main {@link TextComponent} */
    public final TextComponent main = new TextComponent();

    /**
     * New Builder,
     * This method create a new {@link TextComponentBuilder}
     */
    public static TextComponentBuilder builder() {
        return new TextComponentBuilder();
    }

    // text component

    /**
     * To Text Component,
     * This method get the {@link TextComponent} from {@link TextComponentBuilder}
     */
    public TextComponent toTextComponent() {
        this.checkTextComponent();
        return this.main;
    }

    /**
     * Add Text,
     * This method add text to main component
     *
     * @param text the text
     */
    public TextComponentBuilder addText(final String text) {
        this.checkTextComponent();
        this.main.addExtra(text);
        return this;
    }

    /**
     * New Line,
     * This method add a new line text to main component
     */
    public TextComponentBuilder newLine() {
        this.addText("\n");
        return this;
    }

    // hover

    /**
     * Add Hover Text,
     * This method add hover text to main component
     *
     * @param text the text
     * @param hoverText the hover text
     */
    public TextComponentBuilder addHoverShowText(final String text, final String hoverText) {
        this.addHoverEvent(text, hoverText, HoverEvent.Action.SHOW_TEXT);
        return this;
    }

    /**
     * Add Hover Achievement,
     * This method add hover text to main component
     *
     * @param text the text
     * @param hoverText the hover text
     */
    public TextComponentBuilder addHoverShowAchievement(final String text, final String hoverText) {
        this.addHoverEvent(text, hoverText, HoverEvent.Action.SHOW_ACHIEVEMENT);
        return this;
    }

    /**
     * Add Hover Item,
     * This method add hover text to main component
     *
     * @param text the text
     * @param hoverText the hover text
     */
    public TextComponentBuilder addHoverShowItem(final String text, final String hoverText) {
        this.addHoverEvent(text, hoverText, HoverEvent.Action.SHOW_ITEM);
        return this;
    }

    // click

    /**
     * Add Click Run Command,
     * This method add click run command text to main component
     *
     * @param text the text
     * @param command the command
     */
    public TextComponentBuilder addClickRunCommand(final String text, final String command) {
        this.addClickEvent(text, command, ClickEvent.Action.RUN_COMMAND);
        return this;
    }

    /**
     * Add Click Suggest Command,
     * This method add click suggest command text to main component
     *
     * @param text the text
     * @param command the command
     */
    public TextComponentBuilder addClickSuggestCommand(final String text, final String command) {
        this.addClickEvent(text, command, ClickEvent.Action.SUGGEST_COMMAND);
        return this;
    }

    /**
     * Add Click Open URL,
     * This method add click open URL text to main component
     *
     * @param text the text
     * @param url the URL
     */
    public TextComponentBuilder addClickOpenUrl(final String text, final String url) {
        this.addClickEvent(text, url, ClickEvent.Action.OPEN_URL);
        return this;
    }

    /**
     * Add Click Open File,
     * This method add click open file text to main component
     *
     * @param text the text
     * @param file the file
     */
    public TextComponentBuilder addClickOpenFile(final String text, final String file) {
        this.addClickEvent(text, file, ClickEvent.Action.OPEN_FILE);
        return this;
    }

    /**
     * Add Hover And Click Text,
     * This method add a hover event that show a text when hover the text, and when click in text that run command
     *
     * @param text the text
     * @param hoverText the hover text
     * @param command the command
     */
    public TextComponentBuilder addHoverAndClickText(final String text, final String hoverText, final String command) {
        return addHoverAndClickText(text, hoverText, command, false);
    }

    /**
     * Add Hover And Click Text,
     * This method add a hover event that show a text when hover the text, and when click in text that suggest or run command
     * depends by the value from argument {@code suggest}
     *
     * @param text the text
     * @param hoverText the hover text
     * @param command the command
     * @param suggest the suggest command or runnable command
     */
    public TextComponentBuilder addHoverAndClickText(final String text, final String hoverText, final String command, boolean suggest) {
        this.checkTextComponent();

        final TextComponent component = new TextComponent();
        component.setText(text);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText)));
        component.setClickEvent(new ClickEvent(suggest ? ClickEvent.Action.SUGGEST_COMMAND : ClickEvent.Action.RUN_COMMAND, command));

        main.addExtra(component);
        return this;
    }

    // utils

    /**
     * Add Click Text,
     * This method add click text to main component
     *
     * @param text the text
     * @param value the runnable
     */
    private void addClickEvent(final String text, final String value, final ClickEvent.Action action) {
        this.checkTextComponent();

        final TextComponent component = new TextComponent();
        component.setText(text);
        component.setClickEvent(new ClickEvent(action, value));

        main.addExtra(component);
    }

    /**
     * Add Hover Event,
     * This method add hover event with text, hover text and action
     *
     * @param text the text
     * @param hoverText the hover text
     * @param action the action
     */
    private void addHoverEvent(final String text, final String hoverText, final HoverEvent.Action action) {
        this.checkTextComponent();

        final TextComponent component = new TextComponent();
        component.setText(text);
        component.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(hoverText)));

        main.addExtra(component);
    }

    /**
     * Check Text Component,
     * This method check if the {@link #main} have a text
     */
    private void checkTextComponent() {
        if (main.getText() == null || main.getText().length() == 0) {
            main.setText("");
        }
    }
}
