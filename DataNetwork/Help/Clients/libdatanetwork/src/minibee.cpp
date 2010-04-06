/***************************************************************************
 *   This file is part of libdatanetwork                                   *
 *                                                                         *
 *   Copyright (C) 2009 by Marije Baalman                                  *
 *   nescivi _at_ gmail _dot_ com                                          *
 *                                                                         *
 *   This library is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

#include "minibee.h"

namespace SWDataNetwork {

MiniBee::MiniBee( )
{
    id = 0;
    inputSize = 0;
    outputSize = 0;
}

MiniBee::MiniBee( int ident, int insize, int outsize )
{
	id = ident;
	inputSize = insize;
	outputSize = outsize;
}

void MiniBee::setInSize( int sz )
{
	inputSize = sz;
}

void MiniBee::setOutSize( int sz )
{
	outputSize = sz;
}

void MiniBee::setNode( DataNode * dn )
{
	datanode = dn;
}

void MiniBee::setMapped( DataNode * dn )
{
	mappedNode = dn;
}


int MiniBee::getID()
{
	return id;
}

int MiniBee::getOutSize()
{
	return outputSize;
}

int MiniBee::getInSize()
{
	return inputSize;
}

MiniBee::~MiniBee()
{
}


}
