/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.wicket.ui.util;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableBooleanSupplier;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.springframework.lang.Nullable;

import org.apache.isis.applib.Identifier;
import org.apache.isis.commons.internal.base._Strings;
import org.apache.isis.commons.internal.debug._Probe;
import org.apache.isis.commons.internal.debug._Probe.EntryPoint;
import org.apache.isis.commons.internal.functions._Functions.SerializableFunction;
import org.apache.isis.viewer.wicket.model.isis.WicketViewerSettings;
import org.apache.isis.viewer.wicket.ui.panels.PanelUtil;

import lombok.NonNull;
import lombok.val;
import lombok.experimental.UtilityClass;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.checkboxx.CheckBoxX;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.checkboxx.CheckBoxXConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.checkboxx.CheckBoxXConfig.Sizes;
import de.agilecoders.wicket.jquery.Key;

/**
 * Wicket common idioms, in alphabetical order.
 */
@UtilityClass
public class Wkt {

    public <T extends Component> T add(final MarkupContainer container, final T component) {
        container.addOrReplace((Component)component);
        return component;
    }

    public <T extends Behavior> T add(final Component component, final T behavior) {
        component.add((Behavior)behavior);
        return behavior;
    }

    // -- ATTRIBUTES

    /**
     * If any of {@code component} or {@code attributeName} is null or empty, does nothing.
     * On empty {@code attributeValue} removes the attribute.
     */
    public <T extends Component> T attributeReplace(
            final @Nullable T component,
            final @Nullable String attributeName,
            final @Nullable String attributeValue) {
        if(component==null
                || _Strings.isEmpty(attributeName)) {
            return component;
        }
        if(_Strings.isEmpty(attributeValue)) {
            component.add(AttributeModifier.remove(attributeName));
            return component;
        }
        component.add(AttributeModifier.replace(attributeName, attributeValue));
        return component;
    }

    /**
     * If any of {@code component} or {@code attributeName} is null or empty, does nothing.
     * On missing {@code attributeValue} removes the attribute.
     */
    public <T extends Component> T attributeReplace(
            final @Nullable T component,
            final @Nullable String attributeName,
            final @Nullable Integer attributeValue) {
        return attributeReplace(component, attributeName, attributeValue!=null
                ? ""+attributeValue
                : null);
    }

    // -- BEHAVIOR

    public Behavior behaviorOnClick(final SerializableConsumer<AjaxRequestTarget> onClick) {
        return new AjaxEventBehavior("click") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                _Probe.entryPoint(EntryPoint.USER_INTERACTION, "Wicket Ajax Request, "
                        + "originating from User clicking on an "
                        + "editable Property (to start inline editing)"
                        + "or an Action (to enter param negotiaton or directly execute the Action).");

                onClick.accept(target);
            }
        };
    }

    public Behavior behaviorFireOnEscapeKey(final SerializableConsumer<AjaxRequestTarget> onRespond) {
        return new AbstractDefaultAjaxBehavior() {
            private static final long serialVersionUID = 1L;
            private static final String PRE_JS =
                    "" + "$(document).ready( function() { \n"
                            + "  $(document).bind('keyup', function(evt) { \n"
                            + "    if (evt.keyCode == 27) { \n";
            private static final String POST_JS =
                    "" + "      evt.preventDefault(); \n   "
                            + "    } \n"
                            + "  }); \n"
                            + "});";
            @Override public void renderHead(final Component component, final IHeaderResponse response) {
                super.renderHead(component, response);
                final String javascript = PRE_JS + getCallbackScript() + POST_JS;
                response.render(
                        new JavaScriptContentHeaderItem(javascript, null));
            }
            @Override protected void respond(final AjaxRequestTarget target) {
                onRespond.accept(target);
            }
        };
    }

    private static class ReplaceDisabledTagWithReadonlyTagBehavior extends Behavior {
        private static final long serialVersionUID = 1L;
        @Override public void onComponentTag(final Component component, final ComponentTag tag) {
            super.onComponentTag(component, tag);
            if(component.isEnabled()) {
                return;
            }
            tag.remove("disabled");
            tag.put("readonly","readonly");
        }
    }

    public Behavior behaviorReplaceDisabledTagWithReadonlyTag() {
        return new ReplaceDisabledTagWithReadonlyTagBehavior();
    }

    public Behavior behaviorAddOnClick(
            final Component component,
            final SerializableConsumer<AjaxRequestTarget> onClick) {
        return add(component, behaviorOnClick(onClick));
    }

    public Behavior behaviorAddFireOnEscapeKey(
            final Component component,
            final SerializableConsumer<AjaxRequestTarget> onRespond) {
        return add(component, behaviorFireOnEscapeKey(onRespond));
    }

    public void behaviorAddReplaceDisabledTagWithReadonlyTag(final @Nullable Component component) {
        if(component==null) {
            return;
        }
        if (component.getBehaviors(ReplaceDisabledTagWithReadonlyTagBehavior.class).isEmpty()) {
            component.add(new ReplaceDisabledTagWithReadonlyTagBehavior());
        }
    }

    // -- BUTTON

    public AjaxButton button(
            final String id,
            final IModel<String> labelModel,
            final SerializableBiConsumer<AjaxButton, AjaxRequestTarget> onClick) {
        return new AjaxButton(id, labelModel) {
            private static final long serialVersionUID = 1L;
            @Override public void onSubmit(final AjaxRequestTarget target) {
                onClick.accept(this, target);
            }
        };
    }

    public AjaxButton buttonOk(
            final String id,
            final IModel<String> labelModel,
            final WicketViewerSettings settings,
            final SerializableBiConsumer<AjaxButton, AjaxRequestTarget> onClick) {
        return settings.isUseIndicatorForFormSubmit()
        ? new IndicatingAjaxButton(id, labelModel) {
            private static final long serialVersionUID = 1L;
            @Override public void onSubmit(final AjaxRequestTarget target) {
                onClick.accept(this, target);
            }
            @Override protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {
                if (settings.isPreventDoubleClickForFormSubmit()) {
                    PanelUtil.disableBeforeReenableOnComplete(attributes, this);
                }
            }
            @Override protected void onError(final AjaxRequestTarget target) {
                target.add(getForm());
            }
        }
        : new AjaxButton(id, labelModel) {
            private static final long serialVersionUID = 1L;
            @Override public void onSubmit(final AjaxRequestTarget target) {
                onClick.accept(this, target);
            }
            @Override protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {
                if (settings.isPreventDoubleClickForFormSubmit()) {
                    PanelUtil.disableBeforeReenableOnComplete(attributes, this);
                }
            }
            @Override protected void onError(final AjaxRequestTarget target) {
                target.add(getForm());
            }
        };
    }

    public AjaxButton buttonAdd(
            final MarkupContainer markupContainer,
            final String id,
            final IModel<String> labelModel,
            final SerializableBiConsumer<AjaxButton, AjaxRequestTarget> onClick) {
        return add(markupContainer, button(id, labelModel, onClick));
    }

    public AjaxButton buttonAddOk(
            final MarkupContainer markupContainer,
            final String id,
            final IModel<String> labelModel,
            final WicketViewerSettings settings,
            final SerializableBiConsumer<AjaxButton, AjaxRequestTarget> onClick) {
        return add(markupContainer, buttonOk(id, labelModel, settings, onClick));
    }

    // -- CHECKBOX

    /**
     * In correspondence with ScalarPanelFormFieldAbstract.html
     * <wicket:fragment wicket:id="fragment-prompt-checkboxYes">
     */
    final String fragment_prompt_checkboxYes = "<label class=\"fs-4\" style=\"color: green;\">"
            + "<i class=\"fa-regular fa-check-square\"></i></label>";
    /**
     * In correspondence with ScalarPanelFormFieldAbstract.html
     * <wicket:fragment wicket:id="fragment-prompt-checkboxNo">
     */
    final String fragment_prompt_checkboxNo = "<label class=\"fs-4\">"
            + "<i class=\"fa-regular fa-square\"></i></label>";
    /**
     * In correspondence with ScalarPanelFormFieldAbstract.html
     * <wicket:fragment wicket:id="fragment-prompt-checkboxIntermediate">
     */
    final String fragment_prompt_checkboxIntermediate = "<label class=\"fs-4\" style=\"color: silver;\">"
            + "<i class=\"fa-regular fa-square-minus\"></i></label>";

    public static CheckBoxX checkbox(
            final String id,
            final IModel<Boolean> checkedModel,
            final boolean required,
            final Sizes size) {

         final CheckBoxXConfig config = new CheckBoxXConfig() {
            private static final long serialVersionUID = 1L;
            {
                // so can tab to the checkbox
                // not part of the API, so have to use this object initializer
                put(new Key<String>("tabindex"), "0");
            }
        }
        .withSize(size)
        .withEnclosedLabel(false)
        .withIconChecked(fragment_prompt_checkboxYes)
        .withIconNull(fragment_prompt_checkboxIntermediate)
        .withIconUnchecked(fragment_prompt_checkboxNo)
        .withThreeState(!required);

        final CheckBoxX checkBox = new CheckBoxX(id, checkedModel) {

            private static final long serialVersionUID = 1L;

            @Override
            public CheckBoxXConfig getConfig() {
                return config;
            }

            @Override protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                //
                // this is a horrid hack to allow the space bar to work as a way of toggling the checkbox.
                // this hack works for 1.5.4 of the JS plugin (https://github.com/kartik-v/bootstrap-checkbox-x)
                //
                // the problem is that the "change" event is not fired for a keystroke; instead the callback in the
                // JS code (https://github.com/kartik-v/bootstrap-checkbox-x/blob/v1.5.4/js/checkbox-x.js#L70)
                // calls self.change().  This in turn calls validateCheckbox().  In that method it is possible to
                // cause the "change" event to fire, but only if the input element is NOT type="checkbox".
                // (https://github.com/kartik-v/bootstrap-checkbox-x/blob/v1.5.4/js/checkbox-x.js#L132)
                //
                // It's not possible to simply change the associated markup to input type='xx' because it falls foul
                // of a check in super.onComponentTag(tag).  So instead we let that through then hack the tag
                // afterwards:
                //
                tag.put("type", "xx");
            }
        };
        checkBox.setOutputMarkupId(true); // allows AJAX updates to work
        return checkBox;
    }

    // -- CONTAINER

    public WebMarkupContainer container(final String id) {
        final WebMarkupContainer component = new WebMarkupContainer(id);
        component.setOutputMarkupId(true);
        return component;
    }

    public WebMarkupContainer containerWithVisibility(
            final String id,
            final SerializableBooleanSupplier isVisible) {
        final WebMarkupContainer component = new WebMarkupContainer(id) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isVisible() {
                return isVisible.getAsBoolean();
            }
        };
        component.setOutputMarkupId(true);
        return component;
    }

    public WebMarkupContainer containerAdd(final MarkupContainer container, final String id) {
        return add(container, container(id));
    }

    // -- CSS

    /**
     * If {@code cssClass} is empty, does nothing.
     */
    public ComponentTag cssAppend(final ComponentTag tag, final @Nullable String cssClass) {
        if(_Strings.isNotEmpty(cssClass)) {
            tag.append("class", cssClass, " ");
        }
        return tag;
    }

    /**
     * If {@code cssClass} is empty, does nothing.
     */
    public <T extends Component> T cssAppend(final T component, final @Nullable String cssClass) {
        if(_Strings.isNotEmpty(cssClass)) {
            component.add(new CssClassNameAppender(cssClass));
        }
        return component;
    }

    public <T extends Component> T cssAppend(final T component, final @Nullable IModel<String> cssClassModel) {
        if(cssClassModel!=null) {
            component.add(new CssClassNameAppender(cssClassModel));
        }
        return component;
    }

    public <T extends Component> T cssAppend(final T component, final Identifier identifier) {
        return cssAppend(component, cssNormalize(identifier));
    }

    public <T extends Component> T cssReplace(final T component, final @Nullable String cssClass) {
        component.add(AttributeModifier.replace("class", _Strings.nullToEmpty(cssClass)));
        return component;
    }

    public static String cssNormalize(final Identifier identifier) {
        val sb = new StringBuilder();
        sb.append("isis-");
        sb.append(identifier.getLogicalType().getLogicalTypeName());
        if(_Strings.isNullOrEmpty(identifier.getMemberLogicalName())) {
            sb.append("-");
            sb.append(identifier.getMemberLogicalName());
        }
        return cssNormalize(sb.toString());
    }

    public static String cssNormalize(final String cssClass) {
        val trimmed = _Strings.blankToNullOrTrim(cssClass);
        return _Strings.isNullOrEmpty(trimmed)
                ? null
                : cssClass.replaceAll("\\.", "-").replaceAll("[^A-Za-z0-9- ]", "").replaceAll("\\s+", "-");
    }

    // -- DOWNLOAD (RESOURCE LINK)

    public ResourceLinkVolatile downloadLinkNoCache(final String id, final IResource resourceModel) {
        return new ResourceLinkVolatile(id, resourceModel);
    }

    // -- FRAGMENT

    /**
     * @param id - The component id
     * @param fragmentId - The id of the associated markup fragment
     * @param markupProvider - The component whose markup contains the fragment's markup
     */
    public Fragment fragment(final String id, final String fragmentId, final MarkupContainer markupProvider) {
        return new Fragment(id, fragmentId, markupProvider);
    }

    public Fragment fragmentDebug(final String id, final String fragmentId, final MarkupContainer markupProvider) {
        return new Fragment(id, fragmentId, markupProvider) {
            private static final long serialVersionUID = 1L;
            @Override public MarkupContainer add(final Component... children) {
                for(var child:children) {

                    System.err.printf("add %s -> %s %n", this.getId(), child.getId());
                }
                return super.add(children); }
            @Override public MarkupContainer addOrReplace(final Component... children) {
                for(var child:children) {
                    System.err.printf("addOrReplace %s -> %s %n", this.getId(), child.getId());
                }
                return super.addOrReplace(children); }
        };
    }

    /**
     * @param id - The component id
     * @param fragmentId - The id of the associated markup fragment
     * @param markupProvider - The component whose markup contains the fragment's markup
     */
    public Fragment fragmentNoTab(final String id, final String fragmentId, final MarkupContainer markupProvider) {
        return new Fragment(id, fragmentId, markupProvider) {
            private static final long serialVersionUID = 1L;
            @Override protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("tabindex", "-1");}};
    }

    /**
     * @param container - The component to add the fragment to
     * @param id - The component id
     * @param fragmentId - The id of the associated markup fragment
     * @param markupProvider - The component whose markup contains the fragment's markup
     */
    public Fragment fragmentAdd(final MarkupContainer container,
            final String id, final String fragmentId, final MarkupContainer markupProvider) {
        return add(container, fragment(id, fragmentId, markupProvider));
    }

    /**
     * @param container - The component to add the fragment to
     * @param id - The component id
     * @param fragmentId - The id of the associated markup fragment
     * @param markupProvider - The component whose markup contains the fragment's markup
     */
    public Fragment fragmentAddNoTab(final MarkupContainer container,
            final String id, final String fragmentId, final MarkupContainer markupProvider) {
        return add(container, fragmentNoTab(id, fragmentId, markupProvider));
    }

    // -- IMAGE

    public Image imageCachable(final String id, final ResourceReference imageResource) {
        return new Image(id, imageResource) {
            private static final long serialVersionUID = 1L;
            @Override protected boolean shouldAddAntiCacheParameter() { return false; }
        };
    }

    public Image imageAddCachable(final MarkupContainer container, final String id, final ResourceReference imageResource) {
        return add(container, imageCachable(id, imageResource));
    }

    // -- LABEL

    public Label label(final String id, final String label) {
        return new Label(id, label);
    }

    public Label label(final String id, final IModel<String> labelModel) {
        return new Label(id, labelModel);
    }

    public Label labelNoTab(final String id, final IModel<String> labelModel) {
        return new Label(id, labelModel) {
            private static final long serialVersionUID = 1L;
            @Override protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("tabindex", "-1");
            }
        };
    }

    public <T> Label labelWithConverter(
            final String id, final IModel<T> model, final Class<T> type, final IConverter<T> converter) {
        return new Label(id, model) {
            private static final long serialVersionUID = 1L;
            @SuppressWarnings("unchecked")
            @Override public <C> IConverter<C> getConverter(final Class<C> cType) {
                return cType == type
                        ? (IConverter<C>) converter
                        : super.getConverter(cType);}
        };
    }

    public Label labelAdd(final MarkupContainer container, final String id, final String label) {
        return add(container, label(id, label));
    }

    public Label labelAdd(final MarkupContainer container, final String id, final IModel<String> labelModel) {
        return add(container, new Label(id, labelModel));
    }

    public Label labelAddNoTab(final MarkupContainer container, final String id, final IModel<String> labelModel) {
        return add(container, labelNoTab(id, labelModel));
    }

    public <T> Label labelAddWithConverter(
            final MarkupContainer container,
            final String id, final IModel<T> model, final Class<T> type, final IConverter<T> converter) {
        return add(container, labelWithConverter(id, model, type, converter));
    }

    // -- LINK

    public AjaxLink<Void> link(final String id, final SerializableConsumer<AjaxRequestTarget> onClick) {
        return new AjaxLink<Void>(id) {
            private static final long serialVersionUID = 1L;
            @Override public void onClick(final AjaxRequestTarget target) {
                onClick.accept(target);
            }
            @Override protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                fixDisabledState(this, tag);
            }
        };
    }

    /**
     * MOVED over from Wicket 8 - potentially no longer required
     * <p>
     * HACK issue #79: wicket changes tag name if component wasn't enabled
     *
     * @param component the component to fix
     * @param tag       the component tag
     * @deprecated since Wicket 7.0: doesn't mangle the link/button's markup anymore
     */
    @Deprecated
    public static void fixDisabledState(final Component component, final ComponentTag tag) {
        if (!component.isEnabledInHierarchy()) {
            if (component instanceof AbstractLink) {
                tag.setName("a");
            } else if (component instanceof Button) {
                tag.setName("button");
            } else {
                if (tag.getAttribute("value") != null) {
                    tag.setName("input");
                } else {
                    tag.setName("button");
                }
            }

            tag.put("disabled", "disabled");
        }
    }

    public AjaxLink<Void> linkAdd(
            final MarkupContainer container,
            final String id,
            final SerializableConsumer<AjaxRequestTarget> onClick) {
        return add(container, link(id, onClick));
    }

    //    public ActionLink linkAdd(final MarkupContainer container, final String id, final LinkAndLabel linkAndLabel) {
    //        val component = linkAndLabel.getUiComponent();
    //        container.addOrReplace(component);
    //        return (ActionLink) component;
    //    }
    //
    //    public Link<Void> linkAdd(
    //            final MarkupContainer container,
    //            final String linkId,
    //            final String labelId,
    //            final String linkName) {
    //        val link = new Link<Void>(linkId) {
    //            private static final long serialVersionUID = 1L;
    //            @Override
    //            public void onClick() {
    //            }
    //        };
    //        container.addOrReplace(link);
    //        Wkt.labelAdd(link, labelId, linkName);
    //        return link;
    //    }

    // -- LIST VIEW

    public <T> ListView<T> listView(
            final String id,
            final List<T> list,
            final SerializableConsumer<ListItem<T>> itemPopulator) {
        return new ListView<T>(id, list) {
            private static final long serialVersionUID = 1L;
            @Override protected void populateItem(final ListItem<T> item) {
                itemPopulator.accept(item);
            }
        };
    }

    public <T> ListView<T> listView(
            final String id,
            final IModel<? extends List<T>> listModel,
                    final SerializableConsumer<ListItem<T>> itemPopulator) {
        return new ListView<T>(id, listModel) {
            private static final long serialVersionUID = 1L;
            @Override protected void populateItem(final ListItem<T> item) {
                itemPopulator.accept(item);
            }
        };
    }

    public <T> ListView<T> listViewAdd(
            final MarkupContainer container,
            final String id,
            final List<T> list,
            final SerializableConsumer<ListItem<T>> itemPopulator) {
        return add(container, listView(id, list, itemPopulator));
    }

    public <T> ListView<T> listViewAdd(
            final MarkupContainer container,
            final String id,
            final IModel<? extends List<T>> listModel,
                    final SerializableConsumer<ListItem<T>> itemPopulator) {
        return add(container, listView(id, listModel, itemPopulator));
    }

    // -- TABLES

    public <T> Item<T> oddEvenItem(
            final String id, final int index, final IModel<T> model,
            final SerializableFunction<T, String> cssClassProvider) {

        return new OddEvenItem<T>(id, index, model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                Wkt.cssAppend(tag, cssClassProvider.apply(model.getObject()));
            }
        };
    }

    // -- TEXT AREA

    public TextArea<String> textAreaNoTab(final String id, final IModel<String> textModel) {
        return new TextArea<String>(id, textModel) {
            private static final long serialVersionUID = 1L;
            @Override protected void onComponentTag(final ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("tabindex", "-1");
            }
        };
    }

    public TextArea<String> textAreaAddNoTab(
            final MarkupContainer container, final String id, final IModel<String> textModel) {
        return add(container, textAreaNoTab(id, textModel));
    }

    /**
     * @param converter - if {@code null} returns {@link TextArea} using Wicket's default converters.
     */
    public <T> TextArea<T> textAreaWithConverter(
            final String id, final IModel<T> model, final Class<T> type,
            final @Nullable IConverter<T> converter) {
        return converter!=null
            ? new TextArea<T>(id, model) {
                    private static final long serialVersionUID = 1L;
                    {setType(type);}
                    @SuppressWarnings("unchecked")
                    @Override public <C> IConverter<C> getConverter(final Class<C> cType) {
                        return cType == type
                                ? (IConverter<C>) converter
                                : super.getConverter(cType);}
                    @Override public void error(final IValidationError error) {
                        errorMessageIgnoringResourceBundles(this, error);
                    }
                }
            : new TextArea<T>(id, model);
    }

    // -- TEXT FIELD

    /**
     * @param converter - if {@code null} returns {@link TextField} using Wicket's default converters.
     */
    public <T> TextField<T> textFieldWithConverter(
            final String id, final IModel<T> model, final Class<T> type,
            final @Nullable IConverter<T> converter) {
        return converter!=null
            ? new TextField<T>(id, model, type) {
                    private static final long serialVersionUID = 1L;
                    @SuppressWarnings("unchecked")
                    @Override public <C> IConverter<C> getConverter(final Class<C> cType) {
                        return cType == type
                                ? (IConverter<C>) converter
                                : super.getConverter(cType);}
                    @Override public void error(final IValidationError error) {
                        errorMessageIgnoringResourceBundles(this, error);
                    }
                }
            : new TextField<>(id, model, type);
    }

    public <T> TextField<T> passwordFieldWithConverter(
            final String id, final IModel<T> model, final Class<T> type,
            final @NonNull IConverter<T> converter) {
        return new TextField<T>(id, model, type) {
            private static final long serialVersionUID = 1L;
            @SuppressWarnings("unchecked")
            @Override public <C> IConverter<C> getConverter(final Class<C> cType) {
                return cType == type
                        ? (IConverter<C>) converter
                        : super.getConverter(cType);}
            @Override public void error(final IValidationError error) {
                errorMessageIgnoringResourceBundles(this, error);
            }
            @Override protected void onComponentTag(final ComponentTag tag) {
                Attributes.set(tag, "type", "password");
                super.onComponentTag(tag);
            }
            @Override protected String[] getInputTypes() {
                return new String[] {"password"};
            }
        };

    }

    // -- FOCUS UTILITY

    /**
     * If the container has any child with the marker attribute {@code data-isis-focus},
     * then the first one found will receive focus (in the browser).
     * @implNote HTML allows for custom attributes with naming convention {@code data-}.
     */
    public void focusOnMarkerAttribute(
            final MarkupContainer container,
            final AjaxRequestTarget target) {

        container.streamChildren()
        .filter(child->child.getMarkupAttributes().containsKey("data-isis-focus"))
        .findFirst()
        .ifPresent(child->{
            target.focusComponent(child);
        });

    }

    // -- JAVA SCRIPT UTILITY

    public enum EventTopic {
        FOCUS_FIRST_PROPERTY,
        FOCUS_FIRST_PARAMETER,
        OPEN_SELECT2,
        CLOSE_SELECT2,
    }

    public void javaScriptAdd(final AjaxRequestTarget target, final EventTopic topic, final String containerId) {
        target.appendJavaScript(javaScriptFor(topic, containerId));
    }

    public void javaScriptAdd(final IHeaderResponse response, final EventTopic topic, final String containerId) {
        response.render(OnDomReadyHeaderItem.forScript(javaScriptFor(topic, containerId)));
    }

    private String javaScriptFor(final EventTopic topic, final String containerId) {
        return _Strings.isNotEmpty(containerId)
                ? String.format("Wicket.Event.publish(Isis.Topic.%s, '%s')", topic.name(), containerId)
                : String.format("Wicket.Event.publish(Isis.Topic.%s)", topic.name());
    }

    // -- TABBING UTILITY

    public Component noTabbing(final @Nullable Component component) {
        if(component != null) {
            component.add(new AttributeAppender("tabindex", "-1"));
        }
        return component;
    }

    // -- ERROR MESSAGE UTILITY

    /**
     * Reports a validation error against given form component.
     * Uses plain error message from ConversionException, circumventing resource bundles.
     */
    private void errorMessageIgnoringResourceBundles(
            final @Nullable FormComponent<?> formComponent,
            final @Nullable IValidationError error) {
        if(formComponent==null
                || error==null) {
            return;
        }
        if(error instanceof ValidationError) {
            val message = ((ValidationError)error).getMessage();
            // use plain error message from ConversionException, circumventing resource bundles.
            if(_Strings.isNotEmpty(message)) {
                formComponent.error(message);
            } else {
                formComponent.error("Unspecified error (no message associated).");
            }
        } else {
            formComponent.error(error);
        }
    }

}
