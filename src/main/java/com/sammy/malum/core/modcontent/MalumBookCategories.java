package com.sammy.malum.core.modcontent;

import com.sammy.malum.common.book.categories.BookCategory;
import com.sammy.malum.common.book.categories.DarkArtsCategory;
import com.sammy.malum.common.book.categories.DiscoveryCategory;

import java.util.ArrayList;

public class MalumBookCategories
{
    public static ArrayList<BookCategory> CATEGORIES;
    public static void init()
    {
        CATEGORIES = new ArrayList<>();
        CATEGORIES.add(new DiscoveryCategory());
    }
    
}
