# Discovr
A personalized UBC companion app.



# UI Structure and Design


The topmost view is based on Android's Drawer activity template. Each clickable button on the drawer will activate/modify a fragment on the main view.
[Fragment Reference] (https://developer.android.com/guide/components/fragments.html)


## List of Components

**Fragments:**
- EventsSubscribedFragment
- MapViewFragment


**Layouts:**
- activity_main
- app_bar_main
- drawer_layout
- fragment_events_subscribed
- fragment_map_view
- nav_header_main


**Menu:**
- drawer_view
- menu_main

To keep design consistent, if defining colours try to define it by a resource id, i.e.
```
android:color="@color/background_color"
android:textcolor="@color/primary_text_color"
```






